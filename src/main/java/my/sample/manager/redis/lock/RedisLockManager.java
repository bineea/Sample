package my.sample.manager.redis.lock;

import java.util.concurrent.TimeUnit;

public interface RedisLockManager {

    void redisLock(String key, long timeout, TimeUnit unit);

    void redisUnlock(String key);
}
