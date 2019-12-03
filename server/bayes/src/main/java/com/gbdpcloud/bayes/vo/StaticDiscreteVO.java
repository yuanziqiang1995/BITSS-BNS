package com.gbdpcloud.bayes.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StaticDiscreteVO {
    private String modelId;
    private String userId;
    private String description;
    private String location;
    private String modelName;
    private Date updateTime;
    private List<StaticDiscreteNodeVO> nodeList;
    private List<StaticDiscreteLinkVO>linkList;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public HashMap<String, Integer> getMapIdIndex() {
        return mapIdIndex;
    }

    public void setMapIdIndex(HashMap<String, Integer> mapIdIndex) {
        this.mapIdIndex = mapIdIndex;
    }

    public List<StaticDiscreteNodeVO> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<StaticDiscreteNodeVO> nodeList) {
        this.nodeList = nodeList;
    }
    public List<StaticDiscreteLinkVO> getLinkList() {
        return linkList;
    }
    public void setLinkList(List<StaticDiscreteLinkVO> linkList) {
        this.linkList = linkList;
    }
    public HashMap<String,Integer>mapIdIndex;
    public void setMap(){
        mapIdIndex = new HashMap<>();
        int index = 0;
        for(StaticDiscreteNodeVO node:nodeList){
            mapIdIndex.put(node.getId(), index++);
        }
    }
    @Override
    public String toString() {
        return "StaticDiscreteVO{" +
                "modelId='" + modelId + '\'' +
                ", nodeList=" + nodeList +
                ", linkList=" + linkList +
                '}';
    }
}
