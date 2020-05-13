package com.gbdpcloud.bayes.dao;

import com.gbdpcloud.bayes.entity.DataSets;
import com.gbdpcloud.bayes.entity.DiscreteModel;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiscreteModelDao {

    List<DiscreteModel> getDiscreteModelByID(@Param("id") String id,
                                             @Param("index") Integer index,
                                             @Param("rows") Integer rows);

    List<DiscreteModel> getDiscreteModelByUserID(@Param("id") String id,
                                                 @Param("index") Integer index,
                                                 @Param("rows") Integer rows);

    int createDiscreteModel(DiscreteModel discreteModel);

    int deleteDiscreteModel(@Param("id") String id);

    List<DataSets> list(int limit, int offset);

    int delete(String id);
    int update(DiscreteModel param);
    int insert(DiscreteModel param);
    int count();

}
