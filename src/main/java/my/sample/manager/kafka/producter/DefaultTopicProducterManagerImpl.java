package my.sample.manager.kafka.producter;

import org.springframework.stereotype.Service;

@Service
public class DefaultTopicProducterManagerImpl extends AbstractProducterManager implements DefaultTopicProducterManager {

    @Override
    public void send(String topic, String key, String value) {
//        super.send(topic, key, value);
    }
}
