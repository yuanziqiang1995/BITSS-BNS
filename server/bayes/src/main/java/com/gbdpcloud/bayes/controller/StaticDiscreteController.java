package com.gbdpcloud.bayes.controller;


import com.gbdpcloud.bayes.config.Result;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.dao.DiscreteModelDao;
import com.gbdpcloud.bayes.service.StaticDiscreteService;
import com.gbdpcloud.bayes.vo.SearchVO;
import com.gbdpcloud.bayes.vo.StaticDiscreteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class StaticDiscreteController {

    @Autowired
    StaticDiscreteService staticDiscreteService;

    @Autowired
    DiscreteModelDao discreteModelDao;


    @PostMapping("/bayes/static/discrete/model")
    public Result createDiscreteModel(@RequestBody StaticDiscreteVO staticDiscreteNet){

        try {
            staticDiscreteService.networkToFile(staticDiscreteNet);
            return ResultGenerator.ok();
        }
        catch(Exception e){
            return ResultGenerator.error(e.getMessage());
        }
    }

    @DeleteMapping("/bayes/static/discrete/model")
    public Result deleteDiscreteModel(@RequestParam(value = "id") String id) {
        try {
            discreteModelDao.deleteDiscreteModel(id);
            return ResultGenerator.ok();
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
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


    @PostMapping("/bayes/static/discrete/query")
    public Result bayes(@RequestBody SearchVO searchVO){
        String r = staticDiscreteService. queryNetwork(searchVO);
        if(r==null||r.equals(""))
            return ResultGenerator.error("输入网络格式有误");
        return ResultGenerator.ok(r);
    }
}
