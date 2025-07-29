package com.multiagent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@SpringBootApplication
public class CryptoMultiAgentApplication {

    public static void main(String[] args) {
        log.info("Запуск приложения CryptoMultiAgent...");
        SpringApplication.run(CryptoMultiAgentApplication.class, args);
        log.info("Приложение CryptoMultiAgent успешно запущено");
    }
}
