package com.gbdpcloud.bayes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "filepath")
public class FilePathConfig {
    private String models;
    private String input;
    private String datasets;

    public String getModels() {
        return models;
    }

    public void setModels(String models) {
        this.models = models;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getDatasets() {
        return datasets;
    }

    public void setDatasets(String datasets) {
        this.datasets = datasets;
    }
}
