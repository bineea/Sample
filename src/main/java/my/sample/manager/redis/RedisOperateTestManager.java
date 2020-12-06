package my.sample.manager.redis;

import java.util.concurrent.ExecutionException;

public interface RedisOperateTestManager {

    void setStringByRT(String key, String value);

    String getStringByRT(String key);

    void setStringByRedisson(String key, String value);

    String getStringByRedisson(String key) throws ExecutionException, InterruptedException;

    boolean delByRT(String key);

    boolean delByRedisson(String key);

    void redRedisLock();

    void simpleRedisLock();

}
