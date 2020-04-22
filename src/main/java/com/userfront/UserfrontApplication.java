package com.userfront;

import com.userfront.dao.RoleDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class UserfrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserfrontApplication.class, args);
    }
}
