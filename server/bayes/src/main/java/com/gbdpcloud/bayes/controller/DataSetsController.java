package com.gbdpcloud.bayes.controller;

import com.gbdpcloud.bayes.config.FilePathConfig;
import com.gbdpcloud.bayes.config.Result;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.entity.DataSets;
import com.gbdpcloud.bayes.service.DataSetsService;
import com.gbdpcloud.bayes.utils.IdGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bayes/datasets/")
public class DataSetsController {

    @Autowired
    FilePathConfig filePathConfig;

    @Autowired
    DataSetsService dataSetsService;

    @GetMapping("list")
    public Result list(int page, int pageSize) {
        int count = dataSetsService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("list", dataSetsService.list(page, pageSize));
        return ResultGenerator.ok(result);
    }



    @PostMapping("delete")
    public Result delete(String id) {
        return ResultGenerator.ok(dataSetsService.delete(id));
    }

    @PostMapping("update")
    public Result update(DataSets dataSets) {
        return ResultGenerator.ok(dataSetsService.update(dataSets));
    }

    @GetMapping(value = "download", produces = MediaType.ALL_VALUE)
    public void download(@RequestParam("id") String id,
                                     HttpServletResponse response) throws UnsupportedEncodingException {
        DataSets dataSets = dataSetsService.selectById(id);

        String fileName = dataSets.getName();
        if (fileName.indexOf('.')<0){
            fileName = fileName + '.' + dataSets.getFormat();
        }
        String filePath = dataSets.getLocation();
        //下载文件
        if (fileName != null) {
            File file1 = new File(filePath + '\\' + dataSets.getId());
            if (file1.exists()) {
                response.setContentType("application/*");
                response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
                try {
                    InputStream in = new FileInputStream(file1);
                    ServletOutputStream out = response.getOutputStream();
                    final byte[] b = new byte[8192];
                    for (int r; (r = in.read(b)) != -1; ) {
                        out.write(b, 0, r);
                    }
                    in.close();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("upload")
    public Result upload(@RequestParam MultipartFile file,
                         @RequestParam String name,
                         @RequestParam String format,
                         @RequestParam(required = false) String description) {
        System.out.println(name);
        System.out.println(filePathConfig.getDatasets());
        if (file.isEmpty()) {
            return ResultGenerator.error("上传失败，请选择文件");
        }
        try {
            DataSets dataSets = new DataSets();
            dataSets.setName(name);
            dataSets.setFormat(format);
            dataSets.setDescription(description);
            dataSetsService.upload(file, dataSets);
            return ResultGenerator.ok("上传成功");
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
    }

    @PostMapping("")
    public Result createDateset(@RequestBody DataSets dataSets) {
        try {
            dataSetsService.createDateset(dataSets);
            return ResultGenerator.ok();
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
    }
}
