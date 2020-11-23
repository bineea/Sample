package my.sample.manager.redis;

import my.sample.manager.AbstractManager;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class RedisOperateTestManagerImpl extends AbstractManager implements RedisOperateTestManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ValueOperations valueOperations;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void setStringByRT(String key, String value) {
        valueOperations.set(key, value);
    }

    @Override
    public String getStringByRT(String key) {
        Object obj = valueOperations.get(key);
        if(obj != null) {
            return obj.toString();
        }
        return null;
    }

    @Override
    public void setStringByRedisson(String key, String value) {
        RBucket<String> rBucket = redissonClient.getBucket(key);
        rBucket.setAsync(value);
    }

    @Override
    public String getStringByRedisson(String key) throws ExecutionException, InterruptedException {
        RBucket<String> rBucket = redissonClient.getBucket(key);
        String str = rBucket.getAsync().get();
        return str;
    }

    @Override
    public boolean delByRT(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public boolean delByRedisson(String key) {
        return redissonClient.getBucket(key).delete();
    }
}
