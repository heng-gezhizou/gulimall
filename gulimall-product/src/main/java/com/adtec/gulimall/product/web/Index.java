package com.adtec.gulimall.product.web;

import com.adtec.gulimall.product.entity.CategoryEntity;
import com.adtec.gulimall.product.service.CategoryService;
import com.adtec.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class Index {

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String index(Model model){
        List<CategoryEntity> list =  categoryService.getLevel1Categorys();
        model.addAttribute("categorys",list);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String,List<Catalog2Vo>> getCatalog(){
        Map<String,List<Catalog2Vo>> map = categoryService.getCatalog();
        return map;
    }

}
