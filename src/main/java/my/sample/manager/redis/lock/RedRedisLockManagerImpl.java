package my.sample.manager.redis.lock;

import my.sample.manager.AbstractManager;
import my.sample.model.redis.RedisLockModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class RedRedisLockManagerImpl extends AbstractManager implements RedisLockManager {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThreadPoolExecutor mySelfThreadPoolExecutor;

    private ThreadLocal<RedisLockModel> threadLocal = new ThreadLocal<>();

    public boolean tryLockByRedisTemplete(String key, String uuid, long timeout, TimeUnit unit) {

        RedisCallback<Boolean> redisCallback = new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                Expiration expiration = Expiration.from(timeout, unit);
                return connection.set(key.getBytes(), uuid.getBytes(), expiration, RedisStringCommands.SetOption.ifAbsent());
            }
        };

        Boolean result = (Boolean) redisTemplate.execute(redisCallback, true);
        return result == null ? false : result;
    }

    @Override
    public void redisLock(String key, long timeout, TimeUnit unit) {
        for(String uuid = UUID.randomUUID().toString();;) {
            if(tryLockByRedisTemplete(key, uuid, timeout, unit)) {
                RedisLockModel redisLockModel = new RedisLockModel(key, uuid);
                if(redisLockModel.getLocked().get()){
                    Future<?> future = mySelfThreadPoolExecutor.submit(new MyWatchdog(redisTemplate, key, timeout, unit));
                    redisLockModel.setFuture(future);
                    redisLockModel.getLocked().set(false);
                }
                threadLocal.set(redisLockModel);
                System.out.println("Redis分布式锁加锁操作成功，key："+key+"；value："+uuid);
                break;
            } else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean tryUnlockByRedisTemplete_1(String key, String value) {

        RedisCallback<Long> redisCallback = new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {

                String script="if redis.call(\"get\",KEYS[1])==ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return -112345432\n" +
                        "end";

                byte[] keyBytes = key.getBytes();
                byte[] valueBytes = value.getBytes();
                byte[][] keysAndArgs = new byte[keyBytes.length + valueBytes.length][];
                int i;
                for(i = 0; i < keyBytes.length; ++i) {
                    keysAndArgs[i] = keyBytes;
                }
                for(i = 0; i < valueBytes.length; ++i) {
                    keysAndArgs[keyBytes.length + i] = valueBytes;
                }
                return connection.eval(script.getBytes(), ReturnType.INTEGER, 1, keysAndArgs);
            }
        };

        Long result = (Long) redisTemplate.execute(redisCallback, true);
        return result == null || result <= 0 ? false : true;
    }

    public void tryUnlockByRedisTemplete_2(String key, String value) {
        String script="if redis.call(\"get\",KEYS[1])==ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return -112345432\n" +
                "end";
        redisTemplate.execute(RedisScript.of(script, Long.class), Arrays.asList(key), Arrays.asList(value));
    }

    @Override
    public void redisUnlock(String key) {
//        tryUnlockByRedisTemplete(key, threadLocal.get().getValue());
        redisTemplate.delete(key);
        threadLocal.get().getFuture().cancel(true);
        threadLocal.remove();
    }

}
