package com.gbdpcloud.bayes.controller;


import com.gbdpcloud.bayes.config.Result;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.dao.DiscreteModelDao;
import com.gbdpcloud.bayes.entity.DiscreteModel;
import com.gbdpcloud.bayes.entity.StaticDiscreteNet;
import com.gbdpcloud.bayes.service.StaticDiscreteService;
import com.gbdpcloud.bayes.vo.SearchVO;
import com.gbdpcloud.bayes.vo.StaticDiscreteVo;
import org.apache.ibatis.annotations.Delete;
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


    @PostMapping("/bayes/static/discrete/model")
    public Result createDiscreteModel(@RequestBody StaticDiscreteVo staticDiscreteNet){

        staticDiscreteService.networkToFile(staticDiscreteNet);
        return ResultGenerator.ok();
    }

    @DeleteMapping("/bayes/static/discrete/model")
    public Result deleteDiscreteModel(@RequestParam(value = "id") String id){
        discreteModelDao.deleteDiscreteModel(id);
        return ResultGenerator.ok();
    }

    @GetMapping("/bayes/static/discrete/model")
    public Result getDiscreteModelByID(@RequestParam(value = "id") String id,
                                       @RequestParam(value = "pages", defaultValue = "1", required = false) Integer pages,
                                       @RequestParam(value = "rows", defaultValue = "10", required = false) Integer rows){
        try {
            return ResultGenerator.ok(staticDiscreteService.getDiscreteModelByID(id,pages,rows));
        }
        catch (Exception e){
            return ResultGenerator.error(e.getMessage());
        }
    }
    @GetMapping("/bayes/static/discrete/model/user")
    public Result getDiscreteModelByUserID(@RequestParam(value = "id") String id,
                                       @RequestParam(value = "pages", defaultValue = "1", required = false) Integer pages,
                                       @RequestParam(value = "rows", defaultValue = "10", required = false) Integer rows){
        try {
            return ResultGenerator.ok(staticDiscreteService.getDiscreteModelByUserID(id,pages,rows));
        }
        catch (Exception e){
            return ResultGenerator.error(e.getMessage());
        }
    }
//    @GetMapping("/bayes/static/discrete/search")
//    public Result getDiscreteModelByID(@RequestParam(value = "id", defaultValue = "", required = false) String id){
//
//    }

    @PostMapping("/bayes/static/discrete/query")
    public Result bayes(@RequestBody SearchVO searchVO){
        String r = staticDiscreteService. queryNetwork(searchVO);
        if(r==null||r.equals(""))
            return ResultGenerator.error("输入网络格式有误");
        return ResultGenerator.ok(r);
    }
}
