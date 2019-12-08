package my.sample.manager.workflow;

import my.sample.manager.AbstractManager;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sample1TaskListenerManagerImpl extends AbstractManager implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("--------------------触发任务监听器1成功--------------------");
        System.out.println("--------------------"+delegateTask.getVariable("employee")+"--------------------");
        System.out.println("--------------------"+delegateTask.getProcessInstanceId()+"--------------------");
        System.out.println("--------------------触发任务监听器1成功--------------------");
    }
}
