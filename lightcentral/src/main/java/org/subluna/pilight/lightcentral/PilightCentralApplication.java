package org.subluna.pilight.lightcentral;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PilightCentralApplication {

    @Bean
    CommandLineRunner init(FamilyRepository familyRepository, LampRepository lampRepository) {
        return (evt) -> {
            Family family = new Family("testfamily");
            Lamp lamp1 = new Lamp(family, "testlamp 1", true);
            Lamp lamp2 = new Lamp(family, "testlamp 2", false);
            familyRepository.save(family);
            lampRepository.save(lamp1);
            lampRepository.save(lamp2);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(PilightCentralApplication.class, args);
    }

}
