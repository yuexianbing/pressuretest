package com.ybin.pressure.util;

import com.ybin.pressure.model.PressureCondition;
import com.ybin.pressure.model.PressureResult;
import com.ybin.pressure.service.PressureTestService;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yuebing
 * @version 1.0 2017/9/30
 */
@Data
public class PressureUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PressureUtils.class);

    public static void start(PressureCondition condition, PressureTestService testService) {
        Integer threadNum = condition.getThreadNum() == null ? 1 : condition.getThreadNum();
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        CyclicBarrier barrier = new CyclicBarrier(threadNum);
        CountDownLatch downLatch = new CountDownLatch(threadNum);

        PressureResult result = new PressureResult();
        Integer everyThreadNum = condition.getTestNum() / condition.getTestNum();
        result.setEveryThreadNum(everyThreadNum);
        result.setStartTime(new Date());
        result.setTestNum(condition.getTestNum());
        result.setFailAmount(new AtomicInteger(0));
        result.setProcessTime(new AtomicLong(0));
        result.setProcessAmount(new AtomicInteger(0));
        result.setTimeSets(Collections.synchronizedSet(new HashSet<Long>(everyThreadNum)));
        Integer print = condition.getPrint() == null ? 1000 : condition.getPrint();
        for (int thread = 0 ; thread < threadNum ; thread++) {
            PressureRun pressureRun = new PressureRun(barrier , downLatch , print ,result , testService);
            executor.execute(pressureRun);
        }
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            LOG.info("InterruptedException " + e);
        }
        executor.shutdownNow();
        result.setEndTime(new Date());
        result.setTimeLists(new ArrayList<Long>(result.getTimeSets().size()));
        result.getTimeLists().addAll(result.getTimeSets());
        Collections.sort(result.getTimeLists());
        logInfo(result);
        writeFile (condition , result);
    }

    private static void logInfo(PressureResult result ) {
        LOG.info("开始时间: " + result.getStartTime().toString());
        LOG.info("结束时间: " + result.getEndTime().toString());
        LOG.info("每条数据最小处理时间: " + result.getTimeLists().get(0));
        LOG.info("每条数据最大处理时间: " + result.getTimeLists().get(result.getTimeLists().size()-1));
        LOG.info("失败次数: " + result.getFailAmount());
        LOG.info("平均处理时间: " + (float)result.getProcessTime().get()*1000 / result.getTestNum() + "% 每S");
        LOG.info("平均处理速度: " + (float)result.getTestNum() / result.getProcessTime().get()*1000 + "% 每S");
        LOG.info("总耗时: " + result.getProcessTime());
    }

    private static void writeFile(PressureCondition condition, PressureResult result ) {
        if (StringUtils.isBlank(condition.getFilePath()))
            return;
        BufferedWriter writer = null;
        try {
            File file =new File(condition.getFilePath());
            if (!file.exists()) {
                file.createNewFile();
            }

            writer = new BufferedWriter(new FileWriter(file));
            writer.write("开始时间: " + result.getStartTime().toString());
            writer.write("\r\n结束时间: " + result.getEndTime().toString());
            writer.write("\r\n每条数据最小处理时间: " + result.getTimeLists().get(0));
            writer.write("\r\n每条数据最大处理时间: " + result.getTimeLists().get(result.getTimeLists().size()-1));
            writer.write("\r\n失败次数: " + result.getFailAmount());

            writer.write("\r\n平均处理时间: " + (float)result.getProcessTime().get() / result.getTestNum() + " ms/条");
            writer.write("\r\n平均处理速度: " + (float)result.getTestNum() / result.getProcessTime().get()*1000 + " 条/s");
            writer.write("\r\n总耗时: " + result.getProcessTime());
        } catch (IOException e) {
            LOG.info("文件路径找不到!!!");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
