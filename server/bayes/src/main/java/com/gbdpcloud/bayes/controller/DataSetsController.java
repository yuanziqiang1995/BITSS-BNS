package com.gbdpcloud.bayes.controller;

import com.gbdpcloud.bayes.config.FilePathConfig;
import com.gbdpcloud.bayes.config.Result;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.entity.DataSets;
import com.gbdpcloud.bayes.service.DataSetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class DataSetsController {

    @Autowired
    FilePathConfig filePathConfig;

    @Autowired
    DataSetsService dataSetsService;

    @PostMapping("/bayes/datasets/upload")
    public Result uploadDatasets(@RequestBody MultipartFile file){
        System.out.println(filePathConfig.getDatasets());
        if (file.isEmpty()) {
            return ResultGenerator.error("上传失败，请选择文件");
        }
        String fileName = file.getOriginalFilename();
        String filePath = "/Users/ziqiangyuan/Downloads/bayes-new/";
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            return ResultGenerator.ok("上传成功");
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
    }

    @PostMapping("/bayes/datasets")
    public Result createDateset(@RequestBody DataSets dataSets){
        try{
            dataSetsService.createDateset(dataSets);
            return ResultGenerator.ok();
        }
        catch (Exception e){
            return ResultGenerator.error(e.getMessage());
        }
    }
}
