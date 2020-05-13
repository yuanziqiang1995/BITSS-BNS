package com.gbdpcloud.bayes.service;

import com.gbdpcloud.bayes.config.FilePathConfig;
import com.gbdpcloud.bayes.config.ResultGenerator;
import com.gbdpcloud.bayes.dao.DataSetsDao;
import com.gbdpcloud.bayes.entity.DataSets;
import com.gbdpcloud.bayes.utils.IdGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.List;

@Service
public class DataSetsService {
    @Autowired
    DataSetsDao dataSetsDao;

    @Autowired
    FilePathConfig filePathConfig;

    public void createDateset(DataSets dataSets) {
        if (dataSets.getType() == null)
            dataSets.setType(1);
        dataSetsDao.createDataSets(dataSets);
    }

    public int count(){
        return dataSetsDao.count();
    }

    public DataSets selectById(String id){
        return dataSetsDao.selectById(id);
    }

    public int upload(MultipartFile file,DataSets dataSets) throws Exception {
        String fileName = file.getOriginalFilename();
        String id = IdGen.uuid();
        String path = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath() + "\\uploads\\datasets\\" ;
//        String filePath = "/Users/ziqiangyuan/Downloads/bayes-new/";
        File folder = new File(path);

        if (!folder.exists()) {
            folder.mkdirs();
        }
        File dest = new File(path + id);
        file.transferTo(dest);
        dataSets.setId(id);
        dataSets.setType(1);
        dataSets.setLocation(path);
        return dataSetsDao.insert(dataSets);
    }

    public List<DataSets> list(int page, int pageSize) {
        return dataSetsDao.list(pageSize, pageSize * (page - 1));
    }

    public int delete(String id) {
        return dataSetsDao.delete(id);
    }

    public int update(DataSets dataSets) {
        return dataSetsDao.update(dataSets);
    }


}
