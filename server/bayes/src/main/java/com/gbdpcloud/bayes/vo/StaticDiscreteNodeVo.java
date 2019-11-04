package com.gbdpcloud.bayes.vo;

import java.util.List;

public class StaticDiscreteNodeVo {

    //     "id": "common-4f1731dcda61468389f0082097f41558",
//             "nodeName": "节点",
//             "valueNum": "2",
//             "cpt": "0.8, 0,2"
    private String id;
    private String nodeName;
    private String valueNum;
    private List<List<Double>> cpt;
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
                ", cpt=" + cpt +
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

    public List<List<Double>> getCpt() {
        return cpt;
    }

    public void setCpt(List<List<Double>> cpt) {
        this.cpt = cpt;
    }
}
