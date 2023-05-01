package tech.madalingiurca.ordertracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrderTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderTrackingApplication.class, args);
    }

}
