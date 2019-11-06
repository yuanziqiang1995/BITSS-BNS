package com.gbdpcloud.bayes.vo;

import java.util.List;

public class SearchVO {
    private String networkId;
    private String targetId;

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List<EvidenceVO> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<EvidenceVO> evidence) {
        this.evidence = evidence;
    }

    private List<EvidenceVO> evidence;

    @Override
    public String toString(){
        String target = "[\'%s\']";
        String evidences = "evidence={%s}";
        String evidenceStr = "\'%s\':%s";

        StringBuilder eStr = new StringBuilder();
        for(EvidenceVO evidenceVO :evidence){
            if(!eStr.toString().equals("")){
                eStr.append(",");
            }
            eStr.append(String.format(evidenceStr, evidenceVO.getId(), evidenceVO.getValue()));
        }
        evidences = String.format(evidences, eStr);
        target = String.format(target, targetId);
        return target + "," + evidences;
    }
}
