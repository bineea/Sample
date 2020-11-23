package my.sample.web.redis;

import my.sample.manager.redis.RedisOperateTestManager;
import my.sample.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
public class RedisController extends AbstractController {

    @Autowired
    private RedisOperateTestManager redisOperateTestManager;

    @RequestMapping("simpleRedisTemple/set/{key}/{value}")
    public void simpleRedisTempleTest2Set(
            HttpServletResponse response,
            @PathVariable("key") String key,
            @PathVariable("value") String value) throws IOException {

        redisOperateTestManager.setStringByRT(key, value);
        addSuccess(response, "执行set操作后<"+key+">的value为："+value);
    }

    @RequestMapping("simpleRedisTemple/get/{key}")
    public void simpleRedisTempleTest2Get(
            HttpServletResponse response,
            @PathVariable("key") String key) throws IOException {

        String var = redisOperateTestManager.getStringByRT(key);
        addSuccess(response, "执行set操作后<"+key+">的value为："+var);
    }

    @RequestMapping("simpleRedisson/set/{key}/{value}")
    public void simpleRedissonTest2Set(
            HttpServletResponse response,
            @PathVariable("key") String key,
            @PathVariable("value") String value) throws ExecutionException, InterruptedException, IOException {

        redisOperateTestManager.setStringByRedisson(key, value);
        addSuccess(response, "获取<"+key+">的RBucket的value为："+value);
    }

    @RequestMapping("simpleRedisson/get/{key}")
    public void simpleRedissonTest2Get(
            HttpServletResponse response,
            @PathVariable("key") String key) throws ExecutionException, InterruptedException, IOException {

        String str = redisOperateTestManager.getStringByRedisson(key);
        addSuccess(response, "获取<"+key+">的RBucket的value为："+str);
    }
}
