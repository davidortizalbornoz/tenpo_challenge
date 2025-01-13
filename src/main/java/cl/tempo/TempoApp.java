package cl.tempo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@ServletComponentScan
public class TempoApp {
    public static void main(String[] args) {
        SpringApplication.run(TempoApp.class, args);
    }
}

