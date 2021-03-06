package com.gbdpcloud.bayes.service;

import com.gbdpcloud.bayes.dao.DiscreteModelDao;
import com.gbdpcloud.bayes.entity.DataSets;
import com.gbdpcloud.bayes.entity.DiscreteModel;
import com.gbdpcloud.bayes.entity.StaticDiscreteNet;
import com.gbdpcloud.bayes.entity.StaticDiscreteNode;
import com.gbdpcloud.bayes.utils.FileCreater;
import com.gbdpcloud.bayes.utils.IdGen;
import com.gbdpcloud.bayes.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;

@Service
public class StaticDiscreteService {
    private static String folderModel = "models";
    private static String folderInput = "inputs";

    @Autowired
    DiscreteModelDao discreteModelDao;

    public String networkToFile(StaticDiscreteVO staticDiscreteVo){
        String uuid = staticDiscreteVo.getModelId();
        if(uuid == null){
            uuid = IdGen.uuid();
            staticDiscreteVo.setModelId(uuid);
        }
        staticDiscreteVo.setMap();
        String location = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath() + "\\uploads\\network\\" ;
        File folder = new File(location);

        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = location + uuid;
        staticDiscreteVo.setLocation(location);
        String input = JSONObject.toJSONString(staticDiscreteVo);
        FileCreater.saveAsFileWriter(fileName + ".json", input);
        DiscreteModel discreteModel = new DiscreteModel();
        BeanUtils.copyProperties(staticDiscreteVo,discreteModel);
        discreteModelDao.createDiscreteModel(discreteModel);

        StaticDiscreteNet staticDiscreteNet = new StaticDiscreteNet();
        for(StaticDiscreteLinkVO staticDiscreteLinkVo:staticDiscreteVo.getLinkList()){
            int index = staticDiscreteVo.mapIdIndex.get(staticDiscreteLinkVo.getSourceId());
            staticDiscreteNet.edges.add(staticDiscreteVo.getNodeList().get(index).getId());
            index = staticDiscreteVo.mapIdIndex.get(staticDiscreteLinkVo.getTargetId());
            staticDiscreteNet.edges.add(staticDiscreteVo.getNodeList().get(index).getId());
        }
        int k = 0;
        for(StaticDiscreteNodeVO staticDiscreteNodeVo:staticDiscreteVo.getNodeList()){
            StaticDiscreteNode staticDiscreteNode = new StaticDiscreteNode();
            staticDiscreteNode.setVariable(staticDiscreteNodeVo.getId());
            System.out.println(staticDiscreteNode.getVariable());
            staticDiscreteNode.setVariableCard(Integer.valueOf(staticDiscreteNodeVo.getValueNum()));
            staticDiscreteNode.setValues(staticDiscreteNodeVo.getCPT());
            System.out.println(staticDiscreteNode.getValues());
            staticDiscreteNode.setEvidence(staticDiscreteNodeVo.getSequence());
            staticDiscreteNet.variables.add(staticDiscreteNode);
            staticDiscreteNet.MapIdIndex.put(staticDiscreteNode.getVariable(),k++);
        }
        for(StaticDiscreteNode node: staticDiscreteNet.getVariables()){
            node.setEvidenceCard(new ArrayList<>());
            for(String id: node.getEvidence()){
                Integer tempIndex = staticDiscreteNet.MapIdIndex.get(id);
                if(tempIndex!=null)
                    node.evidenceCard.add(staticDiscreteNet.variables.get(tempIndex).getVariableCard());
            }
        }

        FileCreater.saveAsFileWriter(fileName + ".txt", staticDiscreteNet.toString());
//        return staticDiscreteNet.toString();
        return uuid;
    }


    public String queryNetwork(SearchVO searchVO){
        String uuid = searchVO.getNetworkId();
        String modelFile = FileCreater.getFileName(folderModel,uuid);
        String queryFile = FileCreater.getFileName(folderInput,uuid);
        FileCreater.saveAsFileWriter(queryFile, searchVO.toString());
        System.out.println(getResult(modelFile,queryFile));
        String result = getResult(modelFile,queryFile);
        return result.replace("\n","");
    }


    public String getQueryResult(StaticDiscreteNet staticDiscreteNet) {
        String fileName = FileCreater.getFileName(folderModel,"");
        FileCreater.saveAsFileWriter(fileName + ".txt", staticDiscreteNet.toString());
        return getResult(fileName + ".txt","");
    }

    private String getResult(String modelfile, String queryFile) {
        StringBuilder result = new StringBuilder();
        try {
            String[] args = new String[]{"python", "NetworkQuery.py", modelfile, queryFile};
            Process proc = Runtime.getRuntime().exec(args);// 执行py文件
            String temp = "";
            for(String a:args){
                temp+=a + " ";
            }
            System.out.println(temp);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append("\n");
                //System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    public List<StaticDiscreteVO> getDiscreteModelByID(String id, Integer pages, Integer rows){
        Integer index = rows * (pages - 1);
        List<DiscreteModel>  result = discreteModelDao.getDiscreteModelByID(id, index, rows);
        if(result.get(0).getTotal()==0)
            return new ArrayList<>();
        DiscreteModel discreteModel = result.get(0);
        String fileName = discreteModel.getLocation();
        fileName += ".json";
        String file = FileCreater.readFile(fileName);
        StaticDiscreteVO staticDiscreteVo = JSONObject.parseObject(file, StaticDiscreteVO.class);
        BeanUtils.copyProperties(discreteModel, staticDiscreteVo);
        List<StaticDiscreteVO>json = new ArrayList<>();
        json.add(staticDiscreteVo);
        return json;
    }

    public List<DiscreteModel> getDiscreteModelByUserID(String id, Integer pages, Integer rows){
        Integer index = rows * (pages - 1);
        System.out.println(id);
        List<DiscreteModel>  result = discreteModelDao.getDiscreteModelByUserID(id, index, rows);
        if(result.get(0).getTotal()==0)
            return new ArrayList<>();
        return result;
    }

    public int count(){
        return discreteModelDao.count();
    }

    public List<DataSets> list(int page, int pageSize) {
        return discreteModelDao.list(pageSize, pageSize * (page - 1));
    }

    public int delete(String id) {
        return discreteModelDao.delete(id);
    }

    public int update(DiscreteModel discreteModel) {
        return discreteModelDao.update(discreteModel);
    }

}
