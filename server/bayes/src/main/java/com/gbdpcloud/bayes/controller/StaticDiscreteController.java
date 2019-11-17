package com.gbdpcloud.bayes.controller;


import com.gbdpcloud.bayes.config.Result;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.dao.DiscreteModelDao;
import com.gbdpcloud.bayes.entity.DiscreteModel;
import com.gbdpcloud.bayes.entity.StaticDiscreteNet;
import com.gbdpcloud.bayes.service.StaticDiscreteService;
import com.gbdpcloud.bayes.vo.SearchVO;
import com.gbdpcloud.bayes.vo.StaticDiscreteVo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class StaticDiscreteController {

    @Autowired
    StaticDiscreteService staticDiscreteService;

    @Autowired
    DiscreteModelDao discreteModelDao;

    @PostMapping("test/bayes/static/discrete")
    public String testbayes(@RequestBody StaticDiscreteNet staticDiscreteNet){
        return staticDiscreteService.getQueryResult(staticDiscreteNet);
    }

    @PostMapping("test")
    public String testoutput(@RequestBody String a){
        System.out.println(a);
        return a;
    }

    @GetMapping("testid")
    public List<DiscreteModel> testdao(@RequestParam Integer a){
        return discreteModelDao.getDiscreteModelByID(a);
    }

    @PostMapping("/bayes/static/discrete/create")
    public Result bayes(@RequestBody StaticDiscreteVo staticDiscreteNet){
        staticDiscreteService.networkToFile(staticDiscreteNet);
        System.out.println(discreteModelDao.getDiscreteModelByID(1).get(0).toString());
        return ResultGenerator.ok();
    }


    @PostMapping("/bayes/static/discrete/query")
    public Result bayes(@RequestBody SearchVO searchVO){
        String r = staticDiscreteService. queryNetwork(searchVO);
        if(r==null||r.equals(""))
            return ResultGenerator.error("输入网络格式有误");
        return ResultGenerator.ok(r);
    }
}
