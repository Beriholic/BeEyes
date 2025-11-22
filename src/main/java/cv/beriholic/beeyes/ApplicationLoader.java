package cv.beriholic.beeyes;

import org.babyfish.jimmer.client.EnableImplicitApi;
import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories;
import org.babyfish.jimmer.sql.EnableDtoGeneration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableDtoGeneration
@EnableJimmerRepositories
@EnableImplicitApi
@SpringBootApplication
@EnableScheduling
public class ApplicationLoader {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationLoader.class, args);
    }
}
