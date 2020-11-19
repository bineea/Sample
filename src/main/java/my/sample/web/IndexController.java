package my.sample.web;

import my.sample.manager.workflow.WorkflowTestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends AbstractController {

	@RefuseRepeatSubmit
	@RequestMapping("index")
	public String showIndex()
	{
		return "index";
	}
}
