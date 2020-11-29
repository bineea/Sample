package my.sample.manager.ibatis;

import my.sample.dao.model.Oplog;

import java.util.List;

public interface SimpleIbatisManager {

    Oplog simpleOplogData(Integer id);

    Integer simpleOplogAdd();

    Integer simpleOplogAddBatch();
}
