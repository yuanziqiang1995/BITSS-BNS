package com.gbdpcloud.bayes.controller;


import com.gbdpcloud.bayes.entity.StaticDiscreteNet;
import com.gbdpcloud.bayes.entity.StaticDiscreteNode;
import com.gbdpcloud.bayes.service.StaticDiscreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaticDiscreteController {

    @Autowired
    StaticDiscreteService staticDiscreteService;

    @PostMapping("/bayes/static/discrete")
    public String test(@RequestBody StaticDiscreteNet staticDiscreteNet){
        return staticDiscreteService.getQueryResult(staticDiscreteNet);
    }
}
