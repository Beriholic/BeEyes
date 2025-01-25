package xyz.beriholic.beeyes.client.config;


import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.beriholic.beeyes.client.task.MonitorJobBean;

@Slf4j
@Configuration
public class QuartzConfiguration {
    @Bean
    public JobDetail jobDetailFactoryBean() {
        return JobBuilder.newJob(MonitorJobBean.class)
                .withIdentity("monitor-runtime-info")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger cronTriggerFactoryBean(JobDetail jobDetail) {
        CronScheduleBuilder cron = CronScheduleBuilder.cronSchedule("*/10 * * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("monitor-runtime-info-with-cron")
                .withSchedule(cron)
                .build();
    }
}
