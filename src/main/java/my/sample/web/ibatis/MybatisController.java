package my.sample.web.ibatis;

import my.sample.dao.model.Oplog;
import my.sample.manager.ibatis.SimpleIbatisManager;
import my.sample.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class MybatisController extends AbstractController {

    @Autowired
    private SimpleIbatisManager simpleIbatisManager;

    @RequestMapping(value = "simpleMybatisQuery/{id}")
    public String simpleMybatisQuery(@PathVariable("id") Integer id) throws IOException {
        Oplog oplog = simpleIbatisManager.simpleOplogData(id);
        if(oplog == null) {
            System.out.println("null");
        } else {
            System.out.println(oplog.getOp());
        }
        return "success";
    }

    @RequestMapping(value = "simpleMybatisInsert")
    public String simpleMybatisAdd() {
        Integer count = simpleIbatisManager.simpleOplogAdd();
        if(count == null) {
            System.out.println("null");
        } else {
            System.out.println("成功插入数据："+count);
        }
        return "success";
    }

    @RequestMapping(value = "simpleMybatisInsertBatch")
    public String simpleMybatisAddBatch() {
        Integer count = simpleIbatisManager.simpleOplogAddBatch();
        if(count == null) {
            System.out.println("null");
        } else {
            System.out.println("成功插入数据："+count);
        }
        return "success";
    }
}
