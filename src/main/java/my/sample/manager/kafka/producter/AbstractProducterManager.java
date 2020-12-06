package my.sample.manager.kafka.producter;

import my.sample.manager.AbstractManager;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public abstract class AbstractProducterManager extends AbstractManager {

//    @Autowired
//    protected KafkaTemplate kafkaTemplate;
//
//    public void send(String topic, String key, String value) {
//        ListenableFuture send = kafkaTemplate.send(topic, key, value);
//        send.addCallback(new ListenableFutureCallback() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                System.out.println("生产者操作失败，异常信息："+throwable.getMessage());
//            }
//
//            @Override
//            public void onSuccess(Object object) {
//                System.out.println("生产者操作成功，producerRecord："+object.toString());
//            }
//        });
//
//    }

}
