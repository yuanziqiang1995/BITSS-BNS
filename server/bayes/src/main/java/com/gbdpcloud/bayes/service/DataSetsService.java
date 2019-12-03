package com.gbdpcloud.bayes.service;

import com.gbdpcloud.bayes.config.FilePathConfig;
import com.gbdpcloud.bayes.dao.DataSetsDao;
import com.gbdpcloud.bayes.entity.DataSets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSetsService {
    @Autowired
    DataSetsDao dataSetsDao;

    @Autowired
    FilePathConfig filePathConfig;

    public void createDateset(DataSets dataSets){
        if(dataSets.getType()==null)
            dataSets.setType(1);
        dataSetsDao.createDataSets(dataSets);
    }
}
