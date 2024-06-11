package com.example.batch.tasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SingleJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    public SingleJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean("singleStepJob")
    public Job singleStepJob() {
        log.info("--------------Single Step Job-----------------");
        return new JobBuilder("singleStepJob", jobRepository)
                .start(singleStep())
                .build();
    }

    @Bean
    @Qualifier("singleStep")
    public Step singleStep() {
        log.info("----------------Single Step-------------------");
        return new StepBuilder("singleStep", jobRepository)
                .tasklet(singleTasklet(), platformTransactionManager)
                .build();
    }

    @Bean(name = "singleTasklet")
    public Tasklet singleTasklet() {
        return (contribution, chunkContext) -> {
            log.info(">>>>> This is a Single Tasklet <<<<<");

            ExecutionContext stepContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
            if (stepContext.containsKey("taskletStatus")) {
                String status = stepContext.getString("taskletStatus");
                log.info("Previous tasklet status: " + status);
            } else {
                stepContext.put("taskletStatus", "Tasklet completed successfully");
            }

            return RepeatStatus.FINISHED;
        };
    }
}


/**
 * Chunk 처리 단계 : Reader, Processor, Writer
 *
 * execute ->
                   <----> ItemReader
            Step   <--------------------> ItemProcessor
                   <------------------------------------> ItemWriter
 * exitStatus <-
 *
 * Reader : 데이터 소스로부터 데이터 읽어와서 Chunk 생성
 * Processor : 읽어온 데이터에 대한 필요한 처리 수행 (데이터 검증, 필터링, 변환 등)
 * Writer : 처리된 데이터를 최종적으로 저장
 */

//    @Bean
//    public ItemReader<String> reader() {
//        return new ListItemReader<>(Arrays.asList("hyeon", "JinJinJara", "Lee"));
//    }
//
//    @Bean
//    @JobScope
//    public Step inactiveJobStep(@Value("#{jobParameters[requestDate]}") final String requestDate) {
//        log.info("requestDate: {}", requestDate);
//        return stepBuilderFactory.get("inactiveUserStep")
//                .<User, User>chunk(10)
//                .reader(inactiveUserReader())
//                .processor(inactiveUserProcessor())
//                .writer(inactiveUserWriter())
//                .build();
//    }
//}