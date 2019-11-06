package com.gbdpcloud.bayes.vo;

import com.gbdpcloud.bayes.entity.StaticDiscreteNode;

import java.util.HashMap;
import java.util.List;

public class StaticDiscreteVo {
    private String id;

    public HashMap<String,Integer>mapIdIndex;


    public void setMap(){
        mapIdIndex = new HashMap<>();
        int index = 0;
        for(StaticDiscreteNodeVo node:nodeList){
            mapIdIndex.put(node.getId(), index++);
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private List<StaticDiscreteNodeVo> nodeList;
    private List<StaticDiscreteLinkVo>linkList;

    public List<StaticDiscreteNodeVo> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<StaticDiscreteNodeVo> nodeList) {
        this.nodeList = nodeList;
    }

    public List<StaticDiscreteLinkVo> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<StaticDiscreteLinkVo> linkList) {
        this.linkList = linkList;
    }

    @Override
    public String toString() {
        return "StaticDiscreteVo{" +
                "id='" + id + '\'' +
                ", nodeList=" + nodeList +
                ", linkList=" + linkList +
                '}';
    }
}
