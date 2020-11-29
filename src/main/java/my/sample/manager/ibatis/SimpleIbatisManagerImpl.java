package my.sample.manager.ibatis;

import my.sample.dao.ibatis.OplogMapper;
import my.sample.dao.model.Oplog;
import my.sample.manager.AbstractManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleIbatisManagerImpl extends AbstractManager implements SimpleIbatisManager {

    @Autowired
    private OplogMapper oplogMapper;

    @Override
    public Oplog simpleOplogData(Integer id) {
        return oplogMapper.findById(id);
    }

    @Override
    public Integer simpleOplogAdd() {
        Oplog oplog = new Oplog();
        oplog.setUserId("default");
        oplog.setOp("测试code");
        oplog.setOpTime(LocalDateTime.now());
        return oplogMapper.insert(oplog);
    }

    @Override
    public Integer simpleOplogAddBatch() {
        List<Oplog> oplogs = new ArrayList<>();
        for(int i=0; i<10; i++) {
            Oplog oplog = new Oplog();
            oplog.setUserId("default");
            oplog.setOp("测试code"+i+LocalDateTime.now().toString());
            oplog.setOpTime(LocalDateTime.now());
            oplogs.add(oplog);
        }
        return oplogMapper.insertBatch(oplogs);
    }
}
