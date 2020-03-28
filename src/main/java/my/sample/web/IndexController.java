package my.sample.web;

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
