package com.baidu.controller;

import com.baidu.service.PageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.Map;

//@Controller
//@RequestMapping(value = "item")
//@Api(tags = "商品模板展示")
public class PageController {

   //@Autowired
    private PageService pageService;

   //@GetMapping("/test")
    public String test(){
        return "test";
    }

   //@GetMapping("/{spuId}.html")
   // @ApiOperation(value = "获取商品模板信息")
    public String test(@PathVariable(value = "spuId") @NotNull(message = "商品id不能为空") Integer spuId, ModelMap modelMap){
         Map<String,Object> map = pageService.getGoods(spuId);
        modelMap.putAll(map);
        return "item";
    }
}
