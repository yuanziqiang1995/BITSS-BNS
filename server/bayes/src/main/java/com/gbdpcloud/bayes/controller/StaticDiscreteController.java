package com.gbdpcloud.bayes.controller;


import com.gbdpcloud.bayes.config.Result;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.dao.DiscreteModelDao;
import com.gbdpcloud.bayes.entity.DataSets;
import com.gbdpcloud.bayes.entity.DiscreteModel;
import com.gbdpcloud.bayes.service.StaticDiscreteService;
import com.gbdpcloud.bayes.vo.SearchVO;
import com.gbdpcloud.bayes.vo.StaticDiscreteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/bayes/static/discrete/")
public class StaticDiscreteController {

    @Autowired
    StaticDiscreteService staticDiscreteService;

    @Autowired
    DiscreteModelDao discreteModelDao;

    @GetMapping("list")
    public Result list(int page, int pageSize) {
        int count = staticDiscreteService.count();
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("list", staticDiscreteService.list(page, pageSize));
        return ResultGenerator.ok(result);
    }

    @PostMapping("delete")
    public Result delete(String id) {
        return ResultGenerator.ok(staticDiscreteService.delete(id));
    }

    @PostMapping("update")
    public Result update(DiscreteModel discreteModel) {
        return ResultGenerator.ok(staticDiscreteService.update(discreteModel));
    }

    @PostMapping("save")
    public Result createDiscreteModel(@RequestBody StaticDiscreteVO staticDiscreteNet) {

        try {
            staticDiscreteNet.setUserId("1");
            return ResultGenerator.ok(staticDiscreteService.networkToFile(staticDiscreteNet));
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
    }

    @GetMapping("loadModel")
    public Result loadModel(String modelId) {
        String location = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath() + "\\uploads\\network\\";
        File file1 = new File(location + modelId + ".json");
        if (file1.exists()) {
            try {
                StringBuffer buffer = new StringBuffer();
                InputStream in = new FileInputStream(file1);
                String line; // 用来保存每行读取的内容
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                line = reader.readLine(); // 读取第一行
                while (line != null) { // 如果 line 为空说明读完了
                    buffer.append(line); // 将读到的内容添加到 buffer 中
                    buffer.append("\n"); // 添加换行符
                    line = reader.readLine(); // 读取下一行
                }
                reader.close();
                in.close();
                return ResultGenerator.ok(buffer.toString());
            } catch (IOException e) {
                return ResultGenerator.error("读取数据失败");
            }
        }
        return ResultGenerator.error("读取数据失败");
    }

    @DeleteMapping("model")
    public Result deleteDiscreteModel(@RequestParam(value = "id") String id) {
        try {
            discreteModelDao.deleteDiscreteModel(id);
            return ResultGenerator.ok();
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
    }

    @GetMapping("model")
    public Result getDiscreteModelByID(@RequestParam(value = "id") String id,
                                       @RequestParam(value = "pages", defaultValue = "1", required = false) Integer pages,
                                       @RequestParam(value = "rows", defaultValue = "10", required = false) Integer rows) {
        try {
            return ResultGenerator.ok(staticDiscreteService.getDiscreteModelByID(id, pages, rows));
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
    }

    @GetMapping("model/user")
    public Result getDiscreteModelByUserID(@RequestParam(value = "id") String id,
                                           @RequestParam(value = "pages", defaultValue = "1", required = false) Integer pages,
                                           @RequestParam(value = "rows", defaultValue = "10", required = false) Integer rows) {
        try {
            return ResultGenerator.ok(staticDiscreteService.getDiscreteModelByUserID(id, pages, rows));
        } catch (Exception e) {
            return ResultGenerator.error(e.getMessage());
        }
    }


    @PostMapping("query")
    public Result bayes(@RequestBody SearchVO searchVO) {
        String r = staticDiscreteService.queryNetwork(searchVO);
        if (r == null || r.equals(""))
            return ResultGenerator.error("输入网络格式有误");
        return ResultGenerator.ok(r);
    }
}
