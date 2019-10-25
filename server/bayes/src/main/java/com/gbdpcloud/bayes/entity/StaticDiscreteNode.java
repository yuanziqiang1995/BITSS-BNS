package com.gbdpcloud.bayes.entity;

import java.util.List;

public class StaticDiscreteNode {
    private String variable;
    private Integer variableCard;
    private List<List<Double>>values;
    private List<String>evidence;
    private List<Integer>evidenceCard;

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public Integer getVariableCard() {
        return variableCard;
    }

    public void setVariableCard(Integer variableCard) {
        this.variableCard = variableCard;
    }

    public List<List<Double>> getValues() {
        return values;
    }

    public void setValues(List<List<Double>> values) {
        this.values = values;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public List<Integer> getEvidenceCard() {
        return evidenceCard;
    }

    public void setEvidenceCard(List<Integer> evidenceCard) {
        this.evidenceCard = evidenceCard;
    }
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder("variable=\'" + variable + "\'" +
                ", variable_card=" + variableCard +
                ", values=" + values.toString());
        if(evidence!=null){
            str.append(",evidence=[");
            for(int i=0;i<evidence.size();i++){
                String eviStr = "\'%s\'";
                str.append(String.format(eviStr,evidence.get(i)));
                if(i!=evidence.size() - 1)
                    str.append(",");
            }
            str.append("],evidence_card=").append(evidenceCard.toString());
        }
        return str.toString();
    }
}
