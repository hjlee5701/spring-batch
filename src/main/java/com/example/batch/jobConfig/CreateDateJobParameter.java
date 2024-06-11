package com.example.batch.jobConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@NoArgsConstructor
public class CreateDateJobParameter {

    /**
     * JobParameters
     * JobParameters는 JobInstance를 생성하고 구별하는 데 사용되는 파라미터입니다.
     * Job이 실행될 때 필요한 파라미터를 공하며, JobInstance를 구별하는 역할도 합니다.
     * 스프링 배치는 String, Double, Long, Date 이렇게 4가지 타입의 파라미터를 지원합니다.
     */

    @Value("#{jobParameters[status]}")
    private String status;

    private LocalDate createDate;

    @Value("#{jobParameters[createDate]}")
    public void setCreateDate(String createDate) {
        this.createDate = LocalDate.parse(createDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
