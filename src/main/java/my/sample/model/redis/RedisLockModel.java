package my.sample.model.redis;

import my.sample.model.BaseModel;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class RedisLockModel extends BaseModel {
    private String key;
    private String value;
    private Future future;
    private AtomicBoolean locked = new AtomicBoolean(true);

    public RedisLockModel (String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public AtomicBoolean getLocked() {
        return locked;
    }

    public void setLocked(AtomicBoolean locked) {
        this.locked = locked;
    }
}
