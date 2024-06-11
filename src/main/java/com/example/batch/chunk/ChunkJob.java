package com.example.batch.chunk;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
public class ChunkJob {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Autowired
    public ChunkJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    private static final int chunkSize = 5;

    @Autowired
    @Qualifier("userSqlSessionTemplate")
    SqlSessionTemplate userSqlSessionTemplate;

    @Autowired
    @Qualifier("userSqlSessionFactory")
    private SqlSessionFactory userSqlSessionFactory;

    @Autowired
    private SqlSession userSqlSession;

    @Bean(name = "chunkStepJob")
    public Job chunkStepJob(){
        log.info("-----------------Chunk Job--------------------");
        return new JobBuilder("chunkJob", jobRepository)
                .start(chunkStep())
                .build();
    }

    @Bean(name = "chunkStep")
    public Step chunkStep(){
        log.info("----------------Chunk Step--------------------");
        return new StepBuilder("chunkStep", jobRepository)
                .<Map<String,Object>, Map<String,Object>>chunk(chunkSize, transactionManager)
                .reader(itemReader())
//                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    public MyBatisCursorItemReader<Map<String,Object>> itemReader(){
        log.info("-------------Chunk Item Reader----------------");

        MyBatisCursorItemReader<Map<String,Object>> reader = new MyBatisCursorItemReader<>();
        reader.setSqlSessionFactory(userSqlSessionFactory);
        reader.setQueryId("UserMapper.selectUser");
        return reader;
    }

//    public ItemProcessor void itemProcessor() throws Exception{
//        return null;
//
//    }

    public ItemWriter <Map<String,Object>> itemWriter(){
        return items -> {
            for (Map<String, Object> item : items) {
                log.info("Writing item: {}", item);
            }
        };
    }

}
