package com.example.batch.jobConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
@Slf4j
@Configuration
@EnableScheduling
public class JobRunnerConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("singleStepJob")
    private Job singleJob;

    @Autowired
    @Qualifier("multiStepJob")
    private Job multiJob;


    @Scheduled(cron = "0 0/1 * * * ?") // 매 1분마다 실행
    public void scheduleJob1() {
        try {
            log.info("Start Scheduled Job >>> Single Step Job");
            jobLauncher.run(singleJob, new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Scheduled(cron = "0 0/5 * * * ?") // 매 5분마다 실행
    public void scheduleJob2(){
        log.info("Start Scheduled Job >>> Multi Step Job");
        try {
            jobLauncher.run(multiJob, new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
