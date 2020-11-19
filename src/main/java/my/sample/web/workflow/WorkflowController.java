package my.sample.web.workflow;

import my.sample.manager.workflow.WorkflowTestManager;
import my.sample.web.RefuseRepeatSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WorkflowController {

    @Autowired
    private WorkflowTestManager workflowTestManager;

    @RefuseRepeatSubmit
    @RequestMapping("simpleProcessTest")
    public void simpleProcessTest()
    {
        workflowTestManager.simpleProcessTest();
    }
}
