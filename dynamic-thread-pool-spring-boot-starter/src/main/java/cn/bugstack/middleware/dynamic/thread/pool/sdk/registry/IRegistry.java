package cn.bugstack.middleware.dynamic.thread.pool.sdk.registry;

import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.entity.ThreadPoolConfigEntity;

import java.util.List;

public interface IRegistry {
    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntityList);

    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
