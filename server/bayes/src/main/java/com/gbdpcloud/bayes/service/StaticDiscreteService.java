package com.gbdpcloud.bayes.service;

import com.gbdpcloud.bayes.entity.StaticDiscreteNet;
import com.gbdpcloud.bayes.entity.StaticDiscreteNode;
import com.gbdpcloud.bayes.tools.FileCreater;
import com.gbdpcloud.bayes.vo.SearchVO;
import com.gbdpcloud.bayes.vo.StaticDiscreteLinkVo;
import com.gbdpcloud.bayes.vo.StaticDiscreteNodeVo;
import com.gbdpcloud.bayes.vo.StaticDiscreteVo;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Service
public class StaticDiscreteService {
    private static String folderModel = "model";
    private static String folderInput = "inputs";



    public String networkToFile(StaticDiscreteVo staticDiscreteVo){
        String uuid = staticDiscreteVo.getId();
        staticDiscreteVo.setMap();
        String fileName = FileCreater.getFileName(folderModel,uuid);
        StaticDiscreteNet staticDiscreteNet = new StaticDiscreteNet();
        for(StaticDiscreteLinkVo staticDiscreteLinkVo:staticDiscreteVo.getLinkList()){
            int index = staticDiscreteVo.mapIdIndex.get(staticDiscreteLinkVo.getSourceId());
            staticDiscreteNet.edges.add(staticDiscreteVo.getNodeList().get(index).getId());
            index = staticDiscreteVo.mapIdIndex.get(staticDiscreteLinkVo.getTargetId());
            staticDiscreteNet.edges.add(staticDiscreteVo.getNodeList().get(index).getId());
        }
        int k = 0;
        for(StaticDiscreteNodeVo staticDiscreteNodeVo:staticDiscreteVo.getNodeList()){


            StaticDiscreteNode staticDiscreteNode = new StaticDiscreteNode();

            staticDiscreteNode.setVariable(staticDiscreteNodeVo.getId());
            System.out.println(staticDiscreteNode.getVariable());
            staticDiscreteNode.setVariableCard(Integer.valueOf(staticDiscreteNodeVo.getValueNum()));

            staticDiscreteNode.setValues(staticDiscreteNodeVo.getCPT());
            System.out.println(staticDiscreteNode.getValues());
            //staticDiscreteNode.setEvidence(new ArrayList<>());
            staticDiscreteNode.setEvidence(staticDiscreteNodeVo.getSequence());
            //staticDiscreteNode.setEvidence(new ArrayList<>());
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

        FileCreater.saveAsFileWriter(fileName, staticDiscreteNet.toString());
        return staticDiscreteNet.toString();
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
        FileCreater.saveAsFileWriter(fileName, staticDiscreteNet.toString());
        return getResult(fileName,"");
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
}
