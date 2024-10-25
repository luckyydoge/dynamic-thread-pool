package cn.bugstack.middleware.dynamic.thread.pool.sdk.domain;

import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.entity.ThreadPoolConfigEntity;

import java.util.List;

public interface IDynamicThreadPoolService {
    List<ThreadPoolConfigEntity> queryThreadPoolList();

    ThreadPoolConfigEntity queryThreadPoolConfigByName(String name);

    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfig);
}
