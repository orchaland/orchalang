package me;

import orcha.lang.compiler.OrchaCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import me.Me;

@SpringBootApplication
public class MeApplication implements CommandLineRunner {

    @Autowired
    private Me me;

    //@Autowired
    //OrchaCompiler orchaCompiler;

    @Override
    public void run(String... args) throws Exception{
        String message = me.print();
        System.out.println(message);
    }

    public static void main(String[] args) {
        SpringApplication.run(MeApplication.class, args);
    }
}
