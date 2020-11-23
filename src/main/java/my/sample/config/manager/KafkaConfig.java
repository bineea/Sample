package my.sample.config.manager;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean(name = "kafkaProperties")
    public PropertiesFactoryBean loadDatabaseProperties()
    {
        PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
        propertiesFactory.setLocation(new ClassPathResource("config/kafka.properties"));
        propertiesFactory.setFileEncoding("utf-8");
        return propertiesFactory;
    }

    @Value("#{kafkaProperties.brokers}")
    private String brokers;
    @Value("#{kafkaProperties.autoCommitOffset}")
    private String autoCommitOffset;
    @Value("#{kafkaProperties.autoCommitInterval}")
    private String autoCommitInterval;
    @Value("#{kafkaProperties.sessionTimeout}")
    private String sessionTimeout;
    @Value("#{kafkaProperties.groupId}")
    private String groupId;
    @Value("#{kafkaProperties.concurrency}")
    private String concurrency;

    @Value("#{kafkaProperties.ackModel}")
    private String ackModel;
    @Value("#{kafkaProperties.retries}")
    private String retries;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        //消费者并发启动个数（对应分区个数）每个listener方法
        factory.setConcurrency(Integer.parseInt(concurrency));
        factory.getContainerProperties().setPollTimeout(3000);
        //设置批量消费
        //factory.setBatchListener(true);
        //设置AckModel为MANUAL或者MANUAL_IMMEDIATE时，才可以使用acknowledgment.acknowledge();
        //同时设置ENABLE_AUTO_COMMIT_CONFIG为false
        //factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //设置自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.valueOf(autoCommitOffset));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, Integer.parseInt(autoCommitInterval));
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, Integer.parseInt(sessionTimeout));
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    // See https://kafka.apache.org/documentation/#producerconfigs for more properties
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, ackModel);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.parseInt(retries));
        return props;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }

}