package com.gbdpcloud.bayes.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DataSets {
    private String id;
    private String userId;
    private Integer type;
    private String location;
    private String name;
    private Date updateTime;
    private String format;
    private String description;
}
