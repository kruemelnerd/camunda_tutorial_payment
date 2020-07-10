package com.devk.training.camunda.Payment.DistractDiscount;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistractDiscountConfig {

    Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    DistractDiscountHandler distractDiscountHandler;

    @Autowired
    public DistractDiscountConfig(DistractDiscountHandler distractDiscountHandler) {
        this.distractDiscountHandler = distractDiscountHandler;
    }

    @Bean
    void initDistractDiscount(){
        LOGGER.info("DistractDiscountConfig started");
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(5000)
                .usePriority(true)
                .build();

        client.subscribe("distractDiscount")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler(distractDiscountHandler)
                .open();
    }
}
