package com.hjx.search_engine.controller;

import com.hjx.search_engine.entity.ResultItem;
import com.hjx.search_engine.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {


    @Autowired
    private SearchService searchService;

    @GetMapping("/index")
    public ModelAndView index() {

        return new ModelAndView("index");
    }

    @GetMapping("/")
    public String root() {
        return "index";
    }



    /**
     * 接收前端search请求，返回指定位置和数目的排序结果
     * @param keyword,请求关键字
     * @param start,请求要求结果开始位置
     * @param num,请求数量
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> search(@RequestParam("keyword") String keyword,Integer start,Integer num) {
        return searchService.searchHandle(keyword,start,num);
    }

    @RequestMapping(value = "/doSearch",method = RequestMethod.GET)
    public String doSearch(@RequestParam(value = "keyword",required = false) String keyword, Integer start, Integer num, Model model) {
       if(keyword==null) return "search";
        Map<String,Object> mp = searchService.searchHandle(keyword,start,num);
        List<ResultItem> resultItemList = (List<ResultItem>) mp.get("data");
        model.addAttribute("data",resultItemList);
        return "search";
    }
}
