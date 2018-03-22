package com.ybin.pressure.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yuebing
 * @version 1.0 2017/9/30
 */
@Data
public class PressureResult {

    private Date startTime;

    private Date endTime;

    /**
     * 处理时间
     * */
    private AtomicLong processTime;

    private Set<Long> timeSets;

    private List<Long> timeLists;

    /**
     * 平均时间 ms/条
     * */
    private Long averageTime;

    /**
     * 平均处理速度 条/s
     * */
    private Long averageSpeed;

    /**
     * 异常次数
     * */
    private AtomicInteger failAmount;

    /**
     * 线程数
     * */
    private Integer threadNum;

    /**
     * 测试次数
     * */
    private Integer testNum;

    /**
     * 每个线程测试次数
     * */
    private Integer everyThreadNum;

    /**
     * 已处理数量
     * */
    private AtomicInteger processAmount;
}
