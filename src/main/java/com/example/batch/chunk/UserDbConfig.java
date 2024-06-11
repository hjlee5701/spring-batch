package com.example.batch.chunk;

import lombok.extern.slf4j.Slf4j;
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

import javax.sql.DataSource;
@Slf4j
@Configuration
public class UserDbConfig {

    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix="extra-datasource.user.datasource")
    public DataSource dataSource(){
        log.debug("User dataSource bean load");
        return DataSourceBuilder.create().build();
    }

    @Bean(name="userSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("userDataSource") DataSource dbDataSource,
                                               ApplicationContext applicationContext) throws Exception  {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dbDataSource);
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/*.xml"));

        /*
        //스프링 배치에서 커밋 정책은 하나의 디비인데...그 외의 디비는 수동 커밋시키기 위해
        JdbcTransactionFactory trn= new JdbcTransactionFactory();
        trn.newTransaction(dbDataSource, null, false);  //트랜젝션 설정에서 기본 auto commit 값을 false로 해 줍니다.
        sqlSessionFactoryBean.setTransactionFactory(trn);
         */
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "userSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("userSqlSessionFactory") SqlSessionFactory dbSqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(dbSqlSessionFactory, ExecutorType.BATCH);
    }

}
