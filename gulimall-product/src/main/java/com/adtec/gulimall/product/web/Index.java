package com.adtec.gulimall.product.web;

import com.adtec.gulimall.product.entity.CategoryEntity;
import com.adtec.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class Index {

    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/","/index.html"})
    public String index(Model model){
        List<CategoryEntity> list =  categoryService.getLevel1Categorys();
        model.addAttribute("categorys",list);
        return "index";
    }

}
