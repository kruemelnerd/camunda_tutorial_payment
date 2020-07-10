package com.devk.training.camunda.Payment.ChargeCreditCard;

import com.devk.training.camunda.Payment.DistractDiscount.DistractDiscountHandler;
import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChargeCreditConfig {

    Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    ChargeCreditHandler chargeCreditHandler;

    @Autowired
    public ChargeCreditConfig(ChargeCreditHandler chargeCreditHandler) {
        this.chargeCreditHandler = chargeCreditHandler;
    }

    @Bean
    void initChargeCredit(){
        LOGGER.info("DistractDiscountConfig started");
//        ExternalTaskClient client = ExternalTaskClient.create()
//                .baseUrl("http://localhost:8080/engine-rest")
//                .asyncResponseTimeout(5000)
//                .usePriority(true)
//                .build();
//
//        client.subscribe("payCredit")
//                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
//                .handler(chargeCreditHandler)
//                .open();
    }
}
