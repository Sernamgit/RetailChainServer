package ru.otus.prof.retail;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class RetailApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(RetailApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RetailApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {

    }
}
