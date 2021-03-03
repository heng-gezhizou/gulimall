package com.adtec.gulimall.search.web;

import com.adtec.gulimall.search.entity.SearchParam;
import com.adtec.gulimall.search.entity.SearchResult;
import com.adtec.gulimall.search.service.MallSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @Autowired
    private MallSearchService mallSearchService;

    @RequestMapping({"/list.html"})
    public String index(SearchParam param, Model model){
        SearchResult result = mallSearchService.SearchProduct(param);
        model.addAttribute("result",result);
        return "list";
    }

}
