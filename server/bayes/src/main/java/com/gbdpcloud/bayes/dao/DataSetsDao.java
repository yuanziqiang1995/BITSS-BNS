package com.gbdpcloud.bayes.dao;

import com.gbdpcloud.bayes.entity.DataSets;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataSetsDao {
    void createDataSets(DataSets dataSets);
}
