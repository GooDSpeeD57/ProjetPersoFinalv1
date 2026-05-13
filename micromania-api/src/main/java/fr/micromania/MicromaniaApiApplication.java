package fr.micromania;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MicromaniaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicromaniaApiApplication.class, args);
    }
}