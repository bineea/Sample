package my.sample.manager.workflow;

import my.sample.dao.entity.User;
import my.sample.dao.repo.jpa.UserRepo;
import my.sample.manager.AbstractManager;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Sample2ExecutionListenerManagerImpl extends AbstractManager implements ExecutionListener {

    @Autowired
    private UserRepo userRepo;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("============触发执行监听器2开始=============");
        System.out.println("===============当前节点："+delegateExecution.getCurrentActivityId()+"============");
        List<User> userList = userRepo.findAll();
        userList.stream().forEach(u -> logger.info("用户信息--------------"+u.toJson()));
        System.out.println("============触发执行监听器2结束=============");
    }
}
