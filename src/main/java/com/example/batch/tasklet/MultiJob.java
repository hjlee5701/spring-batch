package com.example.batch.tasklet;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class MultiJob {
    // 1개의 JOB
@Autowired
    private final JobRepository jobRepository;

@Autowired
    private final PlatformTransactionManager platformTransactionManager;

    public MultiJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Bean("multiStepJob")
    public Job multiStepJob() {
        log.info("--------------Multi Step Job------------------");
        return new JobBuilder("multiStepJob", jobRepository)
                .start(firstStep())
                .next(secondStep())
                .next(thirdStep())
                .build();
    }
    /**
     *  Multi Step
     * @JobScope
     *      Job 실행 중에 필요한 빈의 생명주기를 관리하는 데 사용
     *      Job 에 속한 모든 JobScope bean 들은 동일 인스턴스를 공유하며 동일 파라미터에 접근 사용할 수 있다.
     *      Job 끝날 때 해당 scope 의 bean 들이 제거되므로 메모리 효율적으로 관리 가능!
     *      각 스레드는 별도의 JobScope bean 인스턴스를 사용하므로 멀티 스레드 환경에서 안전!
     */
    @Bean
    @JobScope
    public Step firstStep(){
        log.info("-----------------firstStep--------------------");

        return new StepBuilder("firstStep", jobRepository)
                .tasklet(multiTasklet(), platformTransactionManager).build();
    }
    @Bean
    @JobScope
    public Step secondStep(){
        log.info("----------------second Step-------------------");

        return new StepBuilder("secondStep", jobRepository)
                .tasklet(multiTasklet(), platformTransactionManager).build();
    }
    @Bean
    @JobScope
    public Step thirdStep(){
        log.info("-----------------third Step-------------------");
        return new StepBuilder("thirdStep", jobRepository)
                .tasklet(multiTasklet(), platformTransactionManager).build();
    }

    /**
     * tasklet 방식 사용
     * @return
     */
    @Bean
//    Step 실행 중에 필요한 빈의 생명주기를 관리
    public Tasklet multiTasklet() {
        return (contribution, chunkContext) -> {
            log.info(">>>>> This is a Multi Tasklet <<<<<");
            // Job Parameters에서 'param' 값을 읽어와서 사용
            JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
            String param = jobParameters.getString("param");

            // chunkContext : 현재 배치 작업의 실행 정보를 포함 (이 정보를 사용하여 작업의 상태를 저장하거나, 다양한 실행 데이터를 관리)
//            ExecutionContext stepContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
//            if (stepContext.containsKey("taskletStatus")) {
//                String status = stepContext.getString("taskletStatus");
//                log.info("Previous tasklet status: " + status);
//            } else {
//                stepContext.put("taskletStatus", "Tasklet completed successfully");
//            }

            return RepeatStatus.FINISHED;
        };
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
}
