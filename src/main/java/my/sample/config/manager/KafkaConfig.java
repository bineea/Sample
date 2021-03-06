package my.sample.config.manager;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Collection;
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

    @Value("#{kafkaProperties.defaultTopic}")
    private String defaultTopic;
    @Value("#{kafkaProperties.partitions}")
    private String partitions;
    @Value("#{kafkaProperties.replicas}")
    private String replicas;

    /********************************************消费者配置*****************************************************/

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
        //设置AckModel为MANUAL或者MANUAL_IMMEDIATE，同时设置ENABLE_AUTO_COMMIT_CONFIG为false，才可以使用acknowledgment.acknowledge()实现手动提交;
        //factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        //设置kafka节点rebalance监听
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                consumer.commitSync();
                System.out.println("kafka节点发生rebalance，当前offset尚未commit时，手动触发consumer提交offset操作");
            }

            @Override
            public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                System.out.println("kafka节点发生rebalance，当前offset已完成commit时，触发方法onPartitionsRevokedAfterCommit");
            }

            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                System.out.println("kafka节点发生rebalance，分区重新分配完成后，consumer开始获取数据时，触发方法onPartitionsAssigned");
            }
        });
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

    /********************************************生产者配置*****************************************************/

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

    /********************************************topic配置*****************************************************/
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic defaultTopic() {
        return TopicBuilder
                .name(defaultTopic)
                .partitions(Integer.parseInt(partitions))
                .replicas(Integer.parseInt(replicas))
                //设置压缩策略
                //.config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
                .build();
    }
}