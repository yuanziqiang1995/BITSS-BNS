package com.gbdpcloud.bayes.dao;

import com.gbdpcloud.bayes.entity.DataSets;
import org.apache.ibatis.annotations.Mapper;

import javax.xml.crypto.Data;
import java.util.List;

@Mapper
public interface DataSetsDao {
    void createDataSets(DataSets dataSets);
    List<DataSets> list(int limit,int offset);
    int delete(String id);
    int update(DataSets param);
    int insert(DataSets param);
    int count();
    DataSets selectById(String id);
}
