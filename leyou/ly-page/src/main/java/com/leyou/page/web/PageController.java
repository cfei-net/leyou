package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 查询商品详情
     * @param spuId     spu的id
     * @return          跳转到页面
     */
    @GetMapping("/item/{id}.html")
    public String toItemHtml(Model model,@PathVariable("id") Long spuId){
        Map<String, Object> data = pageService.loadItemData(spuId);
        model.addAllAttributes(data);
        return "item";
    }

    /**
     *  回顾以前的jsp的处理
     *      前缀：     prefix     /WEB-INF/views
     *      后缀：     suffix     .jsp
     *      方法的返回值，就是jsp的名称： /WEB-INF/views/hello.jsp
     *
     *  Thymeleaf： 和jsp类似 ，也有前缀和后缀 :org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
     *       前缀：    classpath:/templates/
     *       后缀：    .html
     */
    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("msg", "贝吉塔大战三分归元气！");
        return "hello"; // 视图名称
    }
}
