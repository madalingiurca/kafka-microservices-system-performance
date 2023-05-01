package tech.madalingiurca.paymentprocessor.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.bson.UuidRepresentation.STANDARD;

@Configuration
@Slf4j
public class MongoConfiguration {
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.username:paymentsWorker}")
    private String mongoUsername;

    @Value("${spring.data.mongodb.pass:paymentsPass}")
    private char[] mongoPassword;

    @Value("${spring.data.mongodb.database:payments}")
    private String mongoDatabase;

    @Bean
    public MongoClient mongoClient() {

        var mongoClientSettings = MongoClientSettings.builder()
                .uuidRepresentation(STANDARD)
                .credential(MongoCredential.createCredential(mongoUsername, mongoDatabase, mongoPassword))
                .applyConnectionString(new ConnectionString("mongodb://" + mongoHost + ":" + mongoPort))
                .build();

        log.info("""
                Creating mongo client using credentials {} and connection string {}
                """, mongoClientSettings.getCredential(), mongoClientSettings.getConnectionPoolSettings());

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        log.info("Mongo client cluster description: {}", mongoClient.getClusterDescription());
        return new MongoTemplate(mongoClient, mongoDatabase);
    }
}
