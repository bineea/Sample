package my.sample.manager.redis.lock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

import java.util.concurrent.TimeUnit;

public class MyWatchdog implements Runnable {

    private RedisTemplate redisTemplate;
    private String key;
    private long timeout;
    private TimeUnit unit;

    public MyWatchdog(RedisTemplate redisTemplate, String key, long timeout, TimeUnit unit) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void run() {
        while (true) {
            Long timeInSecondes = redisTemplate.getExpire(key);
            if(timeInSecondes != null && timeInSecondes > 0) {
                redisTemplate.expire(key, timeout, unit);
                System.out.println(key+"完成延期操作");
                long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
                try {
                    Thread.sleep(Math.round(rawTimeout * 0.8));
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName()+"中断~~~~~~~~~~~");
                    e.printStackTrace();
                    break;
                }
            } else {
                break;
            }
        }
    }
}
