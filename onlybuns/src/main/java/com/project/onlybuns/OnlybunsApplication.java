package com.project.onlybuns;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class OnlybunsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlybunsApplication.class, args);
	}

}
