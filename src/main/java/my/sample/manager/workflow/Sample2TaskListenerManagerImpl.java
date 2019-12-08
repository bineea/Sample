package my.sample.manager.workflow;

import my.sample.manager.AbstractManager;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;

import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sample2TaskListenerManagerImpl extends AbstractManager implements TaskListener {

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("--------------------触发任务监听器2成功--------------------");
        System.out.println("--------------------"+delegateTask.getVariable("employee")+"--------------------");
        System.out.println("--------------------"+delegateTask.getProcessInstanceId()+"--------------------");
        System.out.println("--------------------触发任务监听器2成功--------------------");

        ExecutionEntity ee = (ExecutionEntity) runtimeService.createExecutionQuery()
                .executionId(delegateTask.getExecutionId()).singleResult();
        if(ee == null)
            System.out.println("无法获取节点信息");
        else
            System.out.println("正常获取节点信息");
    }
}
