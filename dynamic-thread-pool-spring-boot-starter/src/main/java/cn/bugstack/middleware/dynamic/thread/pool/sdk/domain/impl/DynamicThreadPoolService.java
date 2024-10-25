package cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.impl;

import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.IDynamicThreadPoolService;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.entity.ThreadPoolConfigEntity;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class DynamicThreadPoolService implements IDynamicThreadPoolService {

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;
    private final String applicationName;
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolService.class);

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolList() {
        Set<String> beanNameSet = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(beanNameSet.size());
        for (String beanName : beanNameSet) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(beanName);
            ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity(applicationName, beanName);
            threadPoolConfigEntity.copyOf(threadPoolExecutor);
            threadPoolVOS.add(threadPoolConfigEntity);
        }
        return threadPoolVOS;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String name) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(name);
        if (null == threadPoolExecutor) {
            return new ThreadPoolConfigEntity(applicationName, name);
        }
        ThreadPoolConfigEntity threadPoolConfigVO = new ThreadPoolConfigEntity(applicationName, name);
        threadPoolConfigVO.copyOf(threadPoolExecutor);

        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询 应用名:{} 线程名:{} 池化配置:{}", applicationName, name, JSON.toJSONString(threadPoolConfigVO));
        }

        return threadPoolConfigVO;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfig) {
        if (null == threadPoolConfig || !applicationName.equals(threadPoolConfig.getAppName())) {
            return;
        }
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfig.getThreadPoolName());
        if (null == threadPoolExecutor) {
            return;
        }
        threadPoolExecutor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfig.getMaximumPoolSize());
    }
}
