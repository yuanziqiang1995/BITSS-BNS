package com.gbdpcloud.bayes.entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DiscreteModel {
    private String modelId;
    private String modelName;
    private Date updateTime;
    private String userId;
    private String description;
    private Integer total;
    private String location;

    @Override
    public String toString() {
        return "DiscreteModel{" +
                ", modelId='" + modelId + '\'' +
                ", createTime=" + updateTime +
                '}';
    }
}
