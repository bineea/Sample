package my.sample.config.manager;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class KafkaConfig {

    @Bean(name = "kafkaProperties")
    public PropertiesFactoryBean loadDatabaseProperties()
    {
        PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
        propertiesFactory.setLocation(new ClassPathResource("config/kafka.properties"));
        propertiesFactory.setFileEncoding("utf-8");
        return propertiesFactory;
    }


}