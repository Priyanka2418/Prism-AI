package com.aimock.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class
InterviewPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewPlatformApplication.class, args);
	}

}
