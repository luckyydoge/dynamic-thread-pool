package cn.bugstack.middleware.dynamic.thread.pool.sdk.registry.redis;


import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.entity.ThreadPoolConfigEntity;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.valobj.RegistryEnumVO;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.registry.IRegistry;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;

import java.time.Duration;
import java.util.List;


public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntityList) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolConfigEntityList);
    }

    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey().concat("_").concat(threadPoolConfigEntity.getAppName()).concat("_").concat(threadPoolConfigEntity.getThreadPoolName());
        redissonClient.getBucket(cacheKey).set(threadPoolConfigEntity, Duration.ofDays(30));

    }
}
