package com.gbdpcloud.bayes.controller;

import com.gbdpcloud.bayes.config.Result;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.dao.DataSetsDao;
import com.gbdpcloud.bayes.entity.DataSets;
import com.gbdpcloud.bayes.utils.IdGen;
import com.gbdpcloud.bayes.vo.SingleVO;
import com.gbdpcloud.bayes.vo.StaticDiscreteLinkVO;
import com.gbdpcloud.bayes.vo.StaticDiscreteNodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/bayes/network/")
public class NetworkController {

    @Autowired
    DataSetsDao dataSetsDao;

    String home;



    public NetworkController(){
        home = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath();
        System.out.println(home);
    }

    public List<String> runPython(String[] args)throws Exception{
        Process proc = Runtime.getRuntime().exec(args);// 执行py文件

        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(),"gb2312"));
        List<String> result = new ArrayList<>();
        String line = null;
        while ((line = in.readLine()) != null) {
            result.add(line);
        }

        in.close();
        proc.waitFor();
        return result;
    }

    @GetMapping("data")
    public Result data(String datasetId,int page, int pageSize) {
        DataSets dataSets = dataSetsDao.selectById(datasetId);
        String filename = dataSets.getLocation() + datasetId;
        String[] args = new String[]{"python", home + "/../py/data.py",filename, ""+(page-1),""+pageSize};
        try {
            return ResultGenerator.ok(runPython(args));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.error("");
        }
    }

    @GetMapping("init")
    public Result init(String datasetId){
        DataSets dataSets = dataSetsDao.selectById(datasetId);
        String filename = dataSets.getLocation() + datasetId;
        String[] args = new String[]{"python", home + "/../py/read.py", filename};
        try {
            return ResultGenerator.ok(runPython(args));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.error("");
        }
    }

    @PostMapping("learn")
    public Result learn(@RequestParam String datasetId,@RequestParam String edges){
        if (edges == null){
            edges = "\n";
        } else {
            edges += "\n";
        }
        DataSets dataSets = dataSetsDao.selectById(datasetId);
        String tempFileName = IdGen.uuid();
        File f = new File(tempFileName);
        try (Writer output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(f), "UTF-8"))) {
            output.write(edges);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filename = dataSets.getLocation() + datasetId;
        String[] args = new String[]{"python", home + "/../py/learn.py", tempFileName,filename};
        try {
            return ResultGenerator.ok(runPython(args));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.error("");
        } finally {
            f.delete();
        }
    }

    @PostMapping("opt")
    public Result opt(@RequestParam String datasetId,@RequestParam String nodes,@RequestParam String edges){
        if (edges == null){
            edges = "";
        }
        if (nodes == null) {
            nodes = "";
        }
        DataSets dataSets = dataSetsDao.selectById(datasetId);
        String tempFileName = IdGen.uuid();
        File f = new File(tempFileName);
        try (Writer output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(f), "UTF-8"))) {
            output.write(nodes);
            output.write("\n");
            output.write(edges);
            output.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filename = dataSets.getLocation() + datasetId;
        String[] args = new String[]{"python", home + "/../py/opt.py", tempFileName,filename};
        try {
            return ResultGenerator.ok(runPython(args));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.error("");
        } finally {
            f.delete();
        }
    }

    @PostMapping("inference")
    public Result inference(@RequestParam String nodes,@RequestParam String edges,@RequestParam String evidence){
        if (edges == null){
            edges = "";
        }
        if (nodes == null) {
            nodes = "";
        }
        String tempFileName = IdGen.uuid();
        File f = new File(tempFileName);
        try (Writer output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(f), "UTF-8"))) {
            output.write(nodes);
            output.write("\n");
            output.write(edges);
            output.write("\n\n");
            output.write(evidence);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args = new String[]{"python", home + "/../py/inference.py", tempFileName};
        try {
            return ResultGenerator.ok(runPython(args));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.error("");
        } finally {
            f.delete();
        }
    }

    @PostMapping("multi")
    public Result multi(
            @RequestParam String nodes,
            @RequestParam String edges,
            @RequestParam String top,
            @RequestParam String left,
            @RequestParam String right,
            @RequestParam String bottom){
        if (edges == null){
            edges = "";
        }
        if (nodes == null) {
            nodes = "";
        }
        String tempFileName = IdGen.uuid();
        File f = new File(tempFileName);
        try (Writer output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(f), "UTF-8"))) {
            output.write(nodes);
            output.write("\n");
            output.write(edges);
            output.write("\n\n");
            output.write(left);
            output.write("\n\n");
            output.write(top);
            output.write("\n\n");
            output.write(bottom);
            output.write("\n\n");
            output.write(right);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args = new String[]{"python", home + "/../py/multi.py", tempFileName};
        try {
            return ResultGenerator.ok(runPython(args));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.error("");
        } finally {
            f.delete();
        }
    }


    @PostMapping("single")
    public Result single(@RequestBody SingleVO singleVO){
        System.out.println(singleVO);
        Map<String,String> nameMap = new HashMap<>();
        for (StaticDiscreteNodeVO staticDiscreteNodeVO : singleVO.getNodeList()) {
            nameMap.put(staticDiscreteNodeVO.getNodeName(),staticDiscreteNodeVO.getValueNum());
        }
        String tempFileName = IdGen.uuid();
        File f = new File(tempFileName);
        try (Writer output = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(f), "UTF-8"))) {
            for (StaticDiscreteNodeVO staticDiscreteNodeVO : singleVO.getNodeList()) {
                output.write(staticDiscreteNodeVO.getNodeName());
                output.write('\n');
                output.write(staticDiscreteNodeVO.getValueNum());
                output.write('\n');
                output.write(staticDiscreteNodeVO.getId());
                output.write('\n');
                if (staticDiscreteNodeVO.getSequence().size() > 0){
                    output.write(staticDiscreteNodeVO.getSequence().get(0));
                    for(int i=1;i<staticDiscreteNodeVO.getSequence().size();i++){
                        output.write(',');
                        output.write(staticDiscreteNodeVO.getSequence().get(i));
                    }
                }
                output.write('\n');
                if (staticDiscreteNodeVO.getSequence().size() > 0){
                    output.write(nameMap.get(staticDiscreteNodeVO.getSequence().get(0)));
                    for(int i=1;i<staticDiscreteNodeVO.getSequence().size();i++){
                        output.write(',');
                        output.write(nameMap.get(staticDiscreteNodeVO.getSequence().get(i)));
                    }
                }
                output.write('\n');
//                output.write();
            }

            output.write("\n");
            boolean first = true;
            write(output,singleVO.getLinkList(),(o) -> {
                try {
                    output.write(o.getSourceId());
                    output.write(',');
                    output.write(o.getTargetId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            output.write("\n\n");

            write(output,singleVO.getCondition(),(o) -> {
                try {
                    output.write(o);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            output.write("\n\n");

            write(output,singleVO.getNonCondition(),(o) -> {
                try {
                    output.write(o);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

          String[] args = new String[]{"python", home + "/../py/single.py", tempFileName};
        try {

//            return ResultGenerator.ok("[[[0.2045049857549857, 0.7954950142450143], [0.7721170602728201, 0.22788293972717985], [0.3947872084884145, 0.6052127915115856]], [[0.29191072972135634, 0.7080892702786437], [0.43702144200773235, 0.5629785579922677], [0.3903764408845671, 0.609623559115433]], [[0.5300277918072095, 0.4699722081927905], [0.30780463703603, 0.6921953629639699]], [[0.49122807017543857, 0.5087719298245614], [0.20603015075376885, 0.7939698492462312]]]");
                        return ResultGenerator.ok(runPython(args));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.error("");
        } finally {
            f.delete();
        }
    }

    <T> void write(Writer fileWriter, List<T> list, Consumer<T> consumer) throws IOException {
        if (list.size() > 0){
            consumer.accept(list.get(0));
            for(int i=1;i<list.size();i++){
                fileWriter.write(',');
                consumer.accept(list.get(i));
            }
        }
    }

}
