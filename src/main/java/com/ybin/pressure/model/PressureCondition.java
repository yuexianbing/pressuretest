package com.ybin.pressure.model;

import lombok.NonNull;

import java.io.Serializable;

/**
 * @author yuebing
 * @version 1.0 2017/10/9
 */

public class PressureCondition implements Serializable {
    /**
     * 测试数据写入文件路径
     * */
    private String filePath;

    /**
     * 测试数据写入文件名称
     * */
    private String fileName;

    /**
     * 线程数
     * */
    private Integer threadNum;

    /**
     * 测试总次数
     * */
    @NonNull
    private Integer testNum;

    /**
     * 已处理数量达到该变量的倍数打印一条日志
     * */
    private Integer print;

    public String getFilePath() {
        return filePath;
    }

    public PressureCondition setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public PressureCondition setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public PressureCondition setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
        return this;
    }

    public Integer getTestNum() {
        return testNum;
    }

    public PressureCondition setTestNum(Integer testNum) {
        this.testNum = testNum;
        return this;
    }

    public Integer getPrint() {
        return print;
    }

    public PressureCondition setPrint(Integer print) {
        this.print = print;
        return this;
    }
}
