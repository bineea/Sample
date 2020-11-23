package my.sample.manager.kafka.consumer;

import my.sample.manager.AbstractManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class DefaultTopicConsumerManagerImpl extends AbstractManager {

//    @KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = {"default-topic"})
//    public void defaultTopicConsumerListener(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
//        System.out.println("当前消费者消费topic："+record.topic()
//                +"；key："+record.key()
//                +"；value："+record.value()
//                +"；partition："+record.partition()
//                +"；offset："+record.offset()
//        );
//        System.out.println(record.toString());
//        acknowledgment.acknowledge();
//    }

    @KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = {"default-topic"})
    public void defaultTopicConsumerListener(String record) {
        System.out.println("当前消费者消费；value："+record);
    }
}
