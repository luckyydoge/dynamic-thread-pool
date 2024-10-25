package cn.bugstack.middleware.dynamic.thread.pool.trigger;

import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.entity.ThreadPoolConfigEntity;
import cn.bugstack.middleware.dynamic.thread.pool.sdk.domain.models.valobj.RegistryEnumVO;
import cn.bugstack.middleware.dynamic.thread.pool.types.Response;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/dynamic/thread/pool")
public class DynamicThreadPoolController {

    @Resource
    RedissonClient redissonClient;

    @GetMapping("query_thread_pool_list")
    public Response<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        try {
            RList<ThreadPoolConfigEntity> threadPoolConfigEntityList = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .data(threadPoolConfigEntityList.readAll())
                    .build();
        } catch (Exception e) {
            log.error("查询线程池数据异常", e);
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .build();
        }
    }

    @GetMapping("query_thread_pool_config")
    public Response<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam("threadPoolName") String poolName, @RequestParam String appName) {
        try {
            String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + appName + "_" + poolName;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .data(threadPoolConfigEntity)
                    .build();
        } catch (Exception e) {
            log.error("查询线程池配置异常", e);
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("update_thread_pool_config")
    public Response<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity request) {
        try {
            log.info("修改线程池配置开始 {} {} {}", request.getAppName(), request.getThreadPoolName(), JSON.toJSONString(request));
            RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + request.getAppName());
            topic.publish(request);
            log.info("修改线程池配置完成 {} {}", request.getAppName(), request.getThreadPoolName());
            return Response.<Boolean>builder()
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("修改线程池配置异常 {}", JSON.toJSONString(request), e);
            return Response.<Boolean>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }

}
