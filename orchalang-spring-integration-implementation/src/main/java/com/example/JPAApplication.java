package com.example;

import com.example.jpa.EnrollStudent;
import com.example.jpa.PopulateDatabase;
import com.example.jpa.StudentDomain;
import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.*;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.integration.jpa.support.PersistMode;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@SpringBootApplication
public class JPAApplication {

    /*
    receive student from studentDatabase
    enrollStudent with student
    when "enrollStudent terminates"
    send enrollStudent.result to studentOutputDatabase
     */

    @Bean(name = "preprocessingForOrchaCompiler")
    Preprocessing preprocessing(){
        return new PreprocessingImpl();
    }


    @Bean(name="syntaxAnalysisForOrchaCompiler")
    public SyntaxAnalysis syntaxAnalysis() {
        return new SyntaxAnalysisImpl();
    }

    @Bean(name="semanticAnalysisForOrchaCompiler")
    public SemanticAnalysis semanticAnalysis()  {
        return new SemanticAnalysisImpl();
    }

    @Bean(name="postprocessingForOrchaCompiler")
    public Postprocessing postprocessing() {
        return new PostprocessingImpl();
    }
    @Bean(name="lexicalAnalysisForOrchaCompiler")
    @DependsOn({"whenInstruction", "sendInstruction"})
    public LexicalAnalysis lexicalAnalysis() {
        return new LexicalAnalysisImpl();
    }


    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public IntegrationFlow studentDatabaseFlow() {
        return IntegrationFlows
                .from(Jpa.inboundAdapter(this.entityManagerFactory)
                                .entityClass(StudentDomain.class)
                                .maxResults(1)
                                .expectSingleResult(true),
                        e -> e.poller(p -> p.fixedDelay(5000)))
                .enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
                .channel("enrollStudentChannel.input")
                .log()
                .get();
    }

    @Bean(name = "enrollStudent")
    EnrollStudent enrollStudent() {
        return new EnrollStudent();
    }

    @Bean
    MessageToApplication enrollStudentMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "enrollStudent");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow enrollStudentChannel() {
        return f -> f.handle("enrollStudent", "enroll").handle(enrollStudentMessageToApplication(), "transform").channel("aggregateEnrollStudentChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateEnrollStudentChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))")
                .correlationExpression("headers['messageID']"))
                .transform("payload.?[name=='enrollStudent']").handle(applicationToMessage(), "transform").channel("studentOutputDatabaseChannel.input");
    }

    @Bean
    public IntegrationFlow studentOutputDatabaseChannel() {
        return f -> f
                .handle(Jpa.outboundAdapter(this.entityManagerFactory)
                                .entityClass(StudentDomain.class)
                                .persistMode(PersistMode.PERSIST),
                        e -> e.transactional())
                .log();
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(JPAApplication.class, args);

        PopulateDatabase populateDatabase = (PopulateDatabase) context.getBean("populateDatabase");
        //List<?> results = populateDatabase.readDatabase();
        List<?> results;

        System.out.println("\nmanyStudentsInValideTransaction is starting\n");
        try {

            StudentDomain student = new StudentDomain("Morgane", 21, -1);
            populateDatabase.saveStudent(student);
            results = populateDatabase.readDatabase();
            System.out.println("database: " + results);

        } catch (Exception e) {
            System.out.println(">>>>>> Caught exception: " + e);
        }

        //results = populateDatabase.readDatabase();
        //System.out.println("database: " + results);
        //List<?> results = populateDatabase.readDatabase();


    }

}
