package my.sample.common.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisTools {


    @Autowired
    private RedisTemplate redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
}
