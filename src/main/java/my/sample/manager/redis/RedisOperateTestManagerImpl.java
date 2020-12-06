package my.sample.manager.redis;

import my.sample.manager.AbstractManager;
import my.sample.manager.redis.lock.RedRedisLockManagerImpl;
import my.sample.manager.redis.lock.RedisLockManager;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class RedisOperateTestManagerImpl extends AbstractManager implements RedisOperateTestManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ValueOperations valueOperations;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier(value = "redRedisLockManagerImpl")
    private RedisLockManager redRedisLockManager;

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

    public volatile static int a = 0;

    @Override
    public void redRedisLock() {
        String lockStr = "lock_x_add"+UUID.randomUUID().toString();
        CountDownLatch countDownLatch = new CountDownLatch(40);
        for(int x=0; x<40; x++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName()+"执行加锁操作");
                    redRedisLockManager.redisLock(lockStr, 60, TimeUnit.SECONDS);
                    for(int y=0; y<10000 && a<39999; y++) {
                        a++;
                        System.out.println(Thread.currentThread().getName()+"计算a："+a);
                    }
                    System.out.println(Thread.currentThread().getName()+"执行解锁操作");
                    redRedisLockManager.redisUnlock(lockStr);
                    countDownLatch.countDown();
                }
            });
            thread.start();

        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("测试分布式锁："+lockStr+"，a的值应为39999，计算完成后a ："+a);
    }

    @Override
    public void simpleRedisLock() {

    }
}
