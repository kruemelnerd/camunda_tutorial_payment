package com.devk.training.camunda.Payment.config;

import org.camunda.bpm.client.ExternalTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfig {

    Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    void startAllSubscriber(){
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(5000)
                .build();

        client.subscribe("checkCredit")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    LOGGER.info("checkCredit" + " :)");

                    Map<String, Object> variables = new HashMap<String, Object>();
                    variables.put("creditSufficient", true);

                    Object amountObject = externalTask.getVariable("amount");
                    Double amount;
                    if(amountObject instanceof Integer){
                        amount = ((Integer) amountObject).doubleValue();
                    }else if(amountObject instanceof Double){
                        amount = (Double) amountObject;
                    }else if(amountObject instanceof String){
                        amount = Double.parseDouble((String) amountObject);
                    } else {
                        throw new IllegalArgumentException("Amount can't be extracted");
                    }

                    if(amount > 1000d){
                        variables.put("creditSufficient", false);
                    }
                    // Complete the task
                    externalTaskService.complete(externalTask, variables);
                })
                .open();

        //startSingleSubscriber(client, "checkCredit");
        startSingleSubscriber(client, "calculateRates");
        startSingleSubscriber(client, "payCreditCard");
        startSingleSubscriber(client, "payCredit");

    }

    void startSingleSubscriber(ExternalTaskClient client, String subscriberName){
        // subscribe to an external task topic as specified in the process
        client.subscribe(subscriberName)
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    String amount = externalTask.getVariable("amount");
                    Boolean creditSufficient = externalTask.getVariable("creditSufficient");
                    LOGGER.info(subscriberName + ": " + "creditSufficient: " + creditSufficient + " amount: " + amount );
                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }

}
