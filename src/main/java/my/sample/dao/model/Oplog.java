package my.sample.dao.model;

import my.sample.model.BaseModel;

import java.time.LocalDateTime;

public class Oplog extends BaseModel {

    private Integer id;
    private String userId;
    private String op;
    private LocalDateTime opTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public LocalDateTime getOpTime() {
        return opTime;
    }

    public void setOpTime(LocalDateTime opTime) {
        this.opTime = opTime;
    }
}
