package my.sample.web.kafka;

import my.sample.manager.kafka.producter.DefaultTopicProducterManager;
import my.sample.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class KafkaController extends AbstractController {

    @Autowired
    private DefaultTopicProducterManager defaultTopicProducterManager;

    @RequestMapping("simpleKafka/{key}/{value}")
    public String sendDefault(
            HttpServletResponse response,
            @PathVariable("key") String key,
            @PathVariable("value") String value) {
        defaultTopicProducterManager.send("default-topic", key, value);
        return null;
    }
}
