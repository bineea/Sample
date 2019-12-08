package my.sample.manager.workflow;

import my.sample.dao.entity.User;
import my.sample.dao.repo.jpa.UserRepo;
import my.sample.manager.AbstractManager;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

//JAVA服务
@Component
public class SampleServiceManagerImpl extends AbstractManager implements JavaDelegate, Serializable {

    @Autowired
    private UserRepo userRepo;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        delegateExecution.setVariable("MALE_RESULT", "NEUTER");
        logger.info("flowable 自动触发调用JAVA-SERVICE");
        List<User> userList = userRepo.findAll();
        userList.stream().forEach(u -> logger.info("用户信息--------------"+u.toJson()));
    }
}
