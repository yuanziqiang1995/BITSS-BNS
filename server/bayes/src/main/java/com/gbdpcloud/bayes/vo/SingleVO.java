package com.gbdpcloud.bayes.vo;

import lombok.Data;

import java.util.List;

@Data
public class SingleVO {

    List<StaticDiscreteNodeVO> nodeList;

    List<StaticDiscreteLinkVO> linkList;

    List<String> condition;

    List<String> nonCondition;

}
