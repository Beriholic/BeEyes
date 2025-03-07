package xyz.beriholic.beeyes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BeEyesServiceApplication {

    public static void main(String[] args) {
        System.out.println("""
                 ________  _______   _______       ___    ___ _______   ________     \s
                |\\   __  \\|\\  ___ \\ |\\  ___ \\     |\\  \\  /  /|\\  ___ \\ |\\   ____\\    \s
                \\ \\  \\|\\ /\\ \\   __/|\\ \\   __/|    \\ \\  \\/  / | \\   __/|\\ \\  \\___|_   \s
                 \\ \\   __  \\ \\  \\_|/_\\ \\  \\_|/__   \\ \\    / / \\ \\  \\_|/_\\ \\_____  \\  \s
                  \\ \\  \\|\\  \\ \\  \\_|\\ \\ \\  \\_|\\ \\   \\/  /  /   \\ \\  \\_|\\ \\|____|\\  \\ \s
                   \\ \\_______\\ \\_______\\ \\_______\\__/  / /      \\ \\_______\\____\\_\\  \\\s
                    \\|_______|\\|_______|\\|_______|\\___/ /        \\|_______|\\_________\\
                                                 \\|___|/                  \\|_________|
                """);
        SpringApplication.run(BeEyesServiceApplication.class, args);
    }
}
