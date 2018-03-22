package com.ybin.pressure.util;

import com.ybin.pressure.model.PressureResult;
import com.ybin.pressure.service.PressureTestService;
import lombok.Data;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author yuebing
 * @version 1.0 2017/10/9
 */
@Data
public class PressureRun implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PressureRun.class);

    @NonNull
    private PressureTestService testService;

    @NonNull
    private PressureResult result;

    private CyclicBarrier barrier;

    private CountDownLatch downLatch;

    private Integer print;

    public PressureRun(CyclicBarrier barrier , CountDownLatch downLatch , Integer print ,PressureResult result ,
                       PressureTestService testService) {
        this.barrier = barrier;
        this.downLatch = downLatch;
        this.testService = testService;
        this.result = result;
        this.print = print;
    }

    @Override
    public void run() {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        doRun();
        downLatch.countDown();
    }

    private void doRun() {
        for (int i = 0; i < result.getEveryThreadNum(); i++) {
            Long startTime = null;
            try {
                startTime = System.currentTimeMillis();
                testService.doStart();
            }catch (Exception e) {
                result.getFailAmount().incrementAndGet();
                LOG.info("压力测试出现异常 : " + e);
            } finally {
                result.getProcessAmount().incrementAndGet();
                Long endTime = System.currentTimeMillis() - startTime;
                result.getProcessTime().addAndGet(endTime);
                result.getTimeSets().add(endTime);
            }
            if (result.getProcessAmount().get() % print == 0) {
                LOG.info("已处理_" + result.getProcessAmount().get() + "条数据,耗时: " + result.getProcessTime().get());
            }
        }
    }
}
