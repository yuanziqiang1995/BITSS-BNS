package com.gbdpcloud.bayes.service;

import com.gbdpcloud.bayes.entity.StaticDiscreteNet;
import com.gbdpcloud.bayes.tools.FileCreater;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class StaticDiscreteService {
    private static String folder = "/Users/ziqiangyuan/Downloads/bayes/inputs";


    public String getQueryResult(StaticDiscreteNet staticDiscreteNet) {
        String fileName = FileCreater.getFileName(folder);
        FileCreater.saveAsFileWriter(fileName, staticDiscreteNet.toString());
        return getResult(fileName);
    }

    private String getResult(String fileName) {
        StringBuilder result = new StringBuilder();
        try {
            String[] args = new String[]{"python", "NetInput.py", fileName};
            Process proc = Runtime.getRuntime().exec(args);// 执行py文件

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append("\n");
                //System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
