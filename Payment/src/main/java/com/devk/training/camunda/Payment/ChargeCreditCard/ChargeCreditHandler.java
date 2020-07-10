package com.devk.training.camunda.Payment.ChargeCreditCard;

import com.devk.training.camunda.Payment.CheckCredit.CheckCreditService;
import com.devk.training.camunda.Payment.config.ApplicationConfig;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChargeCreditHandler implements ExternalTaskHandler {

    Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    CheckCreditService checkCreditService;

    @Autowired
    public ChargeCreditHandler(CheckCreditService checkCreditService) {
        this.checkCreditService = checkCreditService;
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        LOGGER.info("chargeCredit");

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Boolean shouldFailDuringPayment = externalTask.getVariable("shouldFailDuringPayment");
        if (shouldFailDuringPayment != null && shouldFailDuringPayment) {
            LOGGER.warn("Task ChargeCredit is failing on your own wish");
            externalTaskService.handleBpmnError(externalTask, "CreditError", "Some random message");
            return;
        }
        Double amountToPay = getDoubleValue(externalTask.getVariable("amountToPay"));
        LOGGER.info("Charge Credit with " + amountToPay);

        // Complete the task
        externalTaskService.complete(externalTask);
    }

    private Double getDoubleValue(Object amountObject) {
        Double amount;
        if (amountObject instanceof Integer) {
            amount = ((Integer) amountObject).doubleValue();
        } else if (amountObject instanceof Double) {
            amount = (Double) amountObject;
        } else if (amountObject instanceof String) {
            amount = Double.parseDouble((String) amountObject);
        } else {
            throw new IllegalArgumentException("Amount can't be extracted");
        }
        return amount;
    }
}
