package my.sample.web.workflow;

import my.sample.manager.workflow.WorkflowTestManager;
import my.sample.web.AbstractController;
import my.sample.web.RefuseRepeatSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class WorkflowController extends AbstractController {

    @Autowired
    private WorkflowTestManager workflowTestManager;

    @RefuseRepeatSubmit
    @RequestMapping("simpleProcessOpTest")
    public void simpleProcessTest(HttpServletResponse response) throws IOException {
        workflowTestManager.simpleProcessTest();
        addSuccess(response, "操作工作流成功");
    }
}
