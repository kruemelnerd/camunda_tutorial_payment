package com.devk.training.camunda.Payment.DistractDiscount;

import com.devk.training.camunda.Payment.CheckCredit.CheckCreditService;
import com.devk.training.camunda.Payment.config.ApplicationConfig;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DistractDiscountHandler implements ExternalTaskHandler {

    Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    CheckCreditService checkCreditService;

    @Autowired
    public DistractDiscountHandler(CheckCreditService checkCreditService) {
        this.checkCreditService = checkCreditService;
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        LOGGER.info("distractDiscount");


        Double discount = getDoubleValue(externalTask.getVariable("discount"));
        Double amountToPayWithoutDiscount = getDoubleValue(externalTask.getVariable("amountToPay"));

        Double amountToPay = amountToPayWithoutDiscount - ((amountToPayWithoutDiscount / 100) * discount);

        LOGGER.info("Amount  "+amountToPayWithoutDiscount + " € - " + discount + "% Discount = " + amountToPay);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("amountToPay", amountToPay);
        externalTaskService.complete(externalTask, variables);
    }

    private Double getDoubleValue(Object amountObject) {
        Double amount;
        if( amountObject == null){
            throw new IllegalArgumentException("Amount can't be found. Variable is empty.");
        }else if (amountObject instanceof Integer) {
            amount = ((Integer) amountObject).doubleValue();
        } else if (amountObject instanceof Double) {
            amount = (Double) amountObject;
        } else if (amountObject instanceof String) {
            amount = Double.parseDouble((String) amountObject);
        } else {
            throw new IllegalArgumentException("Amount can't be extracted: " + amountObject.toString());
        }
        return amount;
    }
}
