package com.gbdpcloud.bayes.entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DiscreteModel {
    private Integer id;
    private String modelId;
    private Date createTime;

    @Override
    public String toString() {
        return "DiscreteModel{" +
                "id=" + id +
                ", modelId='" + modelId + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
