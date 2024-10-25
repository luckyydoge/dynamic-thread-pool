package cn.bugstack.middleware.dynamic.thread.pool.sdk.trigger.job;

import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.entity.ThreadPoolConfigEntity;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolReportJob {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolReportJob.class);
    private final IRegistry registry;
    private final IDynamicThreadPoolService dynamicThreadPoolService;

    public ThreadPoolReportJob(IRegistry registry, IDynamicThreadPoolService dynamicThreadPoolService) {
        this.registry = registry;
        this.dynamicThreadPoolService = dynamicThreadPoolService;
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportPoolList() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryThreadPoolList();
        registry.reportThreadPool(threadPoolConfigEntities);
        logger.info("动态线程池，上报线程池信息：{}", JSON.toJSONString(threadPoolConfigEntities));

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("动态线程池，上报线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
        }

    }
}
