package com.devk.training.camunda.Payment.config;

import com.devk.training.camunda.Payment.CheckCredit.CheckCreditHandler;
import com.devk.training.camunda.Payment.CheckCredit.CheckCreditService;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.backoff.BackoffStrategy;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationConfig {

    Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    CheckCreditHandler checkCreditHandler;

    @Autowired
    public ApplicationConfig(CheckCreditHandler checkCreditHandler) {
        this.checkCreditHandler = checkCreditHandler;
    }

    @Bean
    void startAllSubscriber() {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(5000)
                .build();

        client.subscribe("checkCredit")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler(checkCreditHandler)
                .open();

        startSingleSubscriber(client, "calculateRates");
        startSingleSubscriber(client, "payCreditCard");
        startSingleSubscriber(client, "payCredit");

    }

    void startSingleSubscriber(ExternalTaskClient client, String subscriberName) {
        // subscribe to an external task topic as specified in the process
        client.subscribe(subscriberName)
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    String amount = externalTask.getVariable("amountToPay");
                    String userId = externalTask.getVariable("userId");
                    Boolean creditSufficient = externalTask.getVariable("creditSufficient");
                    LOGGER.info(subscriberName + ": " + "userId: " + userId + "creditSufficient: " + creditSufficient + " amountToPay: " + amount);
                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }

}
