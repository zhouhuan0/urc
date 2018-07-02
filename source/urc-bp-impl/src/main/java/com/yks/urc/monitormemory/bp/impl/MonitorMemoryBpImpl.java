/**
 * 〈一句话功能简述〉<br>
 * 〈监控java虚拟内存〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/7/2
 * @since 1.0.0
 */
package com.yks.urc.monitormemory.bp.impl;

import com.yks.urc.monitormemory.bp.api.MonitorMemoryBp;
import com.yks.urc.operation.bp.api.IOperationBp;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MonitorMemoryBpImpl implements MonitorMemoryBp {

    private static Logger logger =Logger.getLogger(MonitorMemoryBpImpl.class);
    @Autowired
    private IOperationBp operationBp;

    private boolean threadSate = true;
    ExecutorService monitorPool = Executors.newFixedThreadPool(4);

    /**
     * 开启内存监控
     *
     * @param
     * @return
     * @Author linwanxian@youkeshu.com
     * @Date 2018/7/2 16:30
     */
    @Override
    public void startMonitor() {
        monitorPool.submit(new Runnable() {
            @Override
            public void run() {
                logger.info("内存监控开始");
                while (threadSate == true) {
                    try {
                        getMemory();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void endMonitor() {
        logger.info("结束监控");
        threadSate = false;
    }

    /**
     * 显示JVM总内存，JVM最大内存和总空闲内存
     */

    public void getMemory() {
        //显示jvm总内存
        long totalMem = Runtime.getRuntime().totalMemory();
        //显示JVM尝试使用的最大内存
        long maxMem = Runtime.getRuntime().maxMemory();
        //空闲内存
        long freeMem = Runtime.getRuntime().freeMemory();
        String message = String.valueOf(totalMem + " " + maxMem + " " + freeMem);
        operationBp.addLog(this.getClass().getName(), message, null);
    }
}
