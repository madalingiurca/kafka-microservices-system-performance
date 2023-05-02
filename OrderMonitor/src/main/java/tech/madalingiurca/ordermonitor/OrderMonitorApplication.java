package tech.madalingiurca.ordermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class OrderMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMonitorApplication.class, args);
    }

}
