package my.sample.manager.ibatis;

import my.sample.dao.ibatis.OplogMapper;
import my.sample.manager.AbstractManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleIbatisManagerImpl extends AbstractManager implements SimpleIbatisManager {

    @Autowired
    private OplogMapper oplogMapper;

    @Override
    public void simpleOplogData() {
        oplogMapper.findById(0);
    }
}
