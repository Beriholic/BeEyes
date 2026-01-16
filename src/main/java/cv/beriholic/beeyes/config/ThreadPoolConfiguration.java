package cv.beriholic.beeyes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfiguration {

    /**
     * IO密集型任务线程池 - 适用于数据库查询、外部API调用等场景
     */
    @Bean("ioIntensiveExecutor")
    public ThreadPoolTaskExecutor ioIntensiveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();

        // IO密集型：核心线程数 = CPU核数 * 2
        int corePoolSize = processors * 2;
        executor.setCorePoolSize(corePoolSize);

        // 最大线程数设置为核心线程数的2倍，应对突发流量
        executor.setMaxPoolSize(corePoolSize * 2);

        // 使用有界队列，避免OOM，容量根据业务负载调整
        executor.setQueueCapacity(200);

        // 突发流量场景，设置较长的存活时间
        executor.setKeepAliveSeconds(60);

        executor.setThreadNamePrefix("io-task-");

        // IO任务通常需要保证可用性，使用调用者执行策略进行降级
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 优雅关闭配置
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * CPU密集型任务线程池 - 适用于计算密集型任务
     */
    @Bean("cpuIntensiveExecutor")
    public ThreadPoolTaskExecutor cpuIntensiveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();

        // CPU密集型：核心线程数 = CPU核数 + 1
        int corePoolSize = processors + 1;
        executor.setCorePoolSize(corePoolSize);

        // CPU密集型任务线程数不宜过多，最大线程数等于核心线程数
        executor.setMaxPoolSize(corePoolSize);

        // 使用较小的有界队列，避免任务堆积影响响应
        executor.setQueueCapacity(50);

        // CPU任务稳定流量，设置较短的存活时间
        executor.setKeepAliveSeconds(10);

        executor.setThreadNamePrefix("cpu-task-");

        // CPU密集型任务可接受丢弃，使用AbortPolicy并配合业务层处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        // 优雅关闭配置
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    /**
     * 混合型任务线程池 - 新增通用场景线程池
     */
    @Bean("commonExecutor")
    public ThreadPoolTaskExecutor commonExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 平衡型配置
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 4);

        // 中等容量队列
        executor.setQueueCapacity(100);

        // 适中存活时间
        executor.setKeepAliveSeconds(30);

        executor.setThreadNamePrefix("common-task-");

        // 通用场景使用丢弃最旧任务策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(45);
        executor.initialize();
        return executor;
    }

    /**
     * 告警通知线程池 - 专用于异步发送告警通知
     * 避免通知延迟或失败阻塞告警检查流程
     */
    @Bean("alertNotificationExecutor")
    public ThreadPoolTaskExecutor alertNotificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 通知任务通常轻量，核心线程数等于CPU核数
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(corePoolSize * 2);

        // 告警通知队列容量相对较大，但也不能无限
        executor.setQueueCapacity(500);

        // 通知任务空闲存活时间
        executor.setKeepAliveSeconds(60);

        executor.setThreadNamePrefix("alert-notify-");

        // 队列满时使用调用者运行策略，避免通知丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}