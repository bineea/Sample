package my.sample.web.redis;

import my.sample.web.AbstractController;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
public class RedisController extends AbstractController {

    @Autowired
    private ValueOperations valueOperations;

    @Autowired
    private RedissonClient redissonClient;

    @RequestMapping("simpleRedisTemple/{key}")
    public void simpleRedisTempleTest(
            HttpServletResponse response,
            @PathVariable("key") String key) throws IOException {
        String str = valueOperations.get(key).toString();
        System.out.println("获取<"+key+">的value为："+str);
        addSuccess(response, "获取<"+key+">的value为："+str);
    }

    @RequestMapping("simpleRedisson/{isSync}/{key}/{value}")
    public void simpleRedissonTest(
            HttpServletResponse response,
            @PathVariable("isSync") boolean isSync,
            @PathVariable("key") String key,
            @PathVariable("value") String value) throws ExecutionException, InterruptedException, IOException {

        String str = null;
        RBucket<String> bucket = redissonClient.getBucket(key);
        System.out.println("当前RBucket的value为："+bucket.get());
        if(isSync) {
            //同步
            bucket.set(value);
            str = bucket.get();
        } else {
            //异步
            bucket.setAsync(value).get();
            bucket.getAsync().thenAccept(System.out::println);
        }
        System.out.println("执行set操作后，<"+key+">的RBucket的value为："+str);
        addSuccess(response, "获取<"+key+">的RBucket的value为："+str);
    }
}
