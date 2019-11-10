package com.gbdpcloud.bayes.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StaticDiscreteNodeVo {

    //     "id": "common-4f1731dcda61468389f0082097f41558",
//             "nodeName": "节点",
//             "valueNum": "2",
//             "CPT": "0.8, 0,2"
    private String id;
    private String nodeName;
    private String valueNum;

    @JsonProperty("CPT")
    private List<List<Double>> CPT;
    public List<String> sequence;

    public List<String> getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "StaticDiscreteNodeVo{" +
                "id='" + id + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", valueNum='" + valueNum + '\'' +
                ", CPT=" + CPT +
                ", sequence=" + sequence +
                '}';
    }

    public void setSequence(List<String> sequence) {
        this.sequence = sequence;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getValueNum() {
        return valueNum;
    }

    public void setValueNum(String valueNum) {
        this.valueNum = valueNum;
    }

    public List<List<Double>> getCPT() {
        return CPT;
    }

    public void setCPT(List<List<Double>> CPT) {
        this.CPT = CPT;
    }
}
