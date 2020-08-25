package schoolMongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mongodb.dsl.MongoDb;

@SpringBootApplication
public class MongodbReadWriteApplication {
    @Autowired
    private MongoConverter mongoConverter;

    @Bean
    MongoDatabaseFactory mongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(), "test");
    }

    @Bean
    public IntegrationFlow studentDatabaseFlow() {
        return f -> f.handle(queryOutboundGateway());
    }

    @Bean
    public IntegrationFlow queryOutboundGateway() {
        return f -> f.handle(MongoDb.outboundGateway(mongoConverter, this.mongoConverter).query("{firstName : 'Marwa'}").collectionNameExpression("student").expectSingleResult(true).entityClass(StudentDomain.class)).enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).channel("enrollStudentChannel.input").log();
    }
    @Bean
    public IntegrationFlow studentOutputDatabaseChannel() {
        return f -> f.handle(mongoOutboundAdapter(mongoDbFactory()));
    }
}
