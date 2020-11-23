package my.sample.manager.kafka.producter;

public interface DefaultTopicProducterManager {

    void send(String topic, String key, String value);
}
