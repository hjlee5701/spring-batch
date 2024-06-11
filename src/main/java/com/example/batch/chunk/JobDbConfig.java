package com.example.batch.chunk;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

//@Slf4j
@Slf4j
@Configuration
@EnableTransactionManagement
public class JobDbConfig {

//    private static final Log log = LogFactory.getLog(JobDbConfig.class);

    @Primary
    @Bean(name="jobDataSource")
    @ConfigurationProperties(prefix="spring.batch.job.datasource")
    public DataSource dataSource() {
        log.debug("dataSource bean load");
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name="jobSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("jobDataSource") DataSource dbDataSource,
                                               ApplicationContext applicationContext) throws Exception  {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dbDataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/*.xml"));

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "jobSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("jobSqlSessionFactory") SqlSessionFactory dbSqlSessionFactory) {
        return new SqlSessionTemplate(dbSqlSessionFactory, ExecutorType.BATCH);
    }

}
