package cn.hejinyo.elasticsearch.controller;

import cn.hejinyo.elasticsearch.test.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: anthony.s.he
 * @Email: hejinyo@gmail.cn
 * @Date: 2018-12-06 11:06
 */
@RestController
@RequestMapping
public class IndexController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/get")
    public String get(){
        demoService.get();
        return "success";
    }

    @GetMapping("/createIndex/{indexName}")
    public String createIndex(@PathVariable("indexName") String indexName){
        demoService.createIndex(indexName);
        return "success";
    }
}
