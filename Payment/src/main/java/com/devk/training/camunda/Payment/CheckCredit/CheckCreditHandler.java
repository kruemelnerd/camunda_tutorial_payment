package com.devk.training.camunda.Payment.CheckCredit;

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
public class CheckCreditHandler implements ExternalTaskHandler {

    Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    CheckCreditService checkCreditService;

    @Autowired
    public CheckCreditHandler(CheckCreditService checkCreditService) {
        this.checkCreditService = checkCreditService;
    }

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        LOGGER.info("checkCredit" + " :)");

        Boolean shouldFail = externalTask.getVariable("shouldFail");
        if (shouldFail != null && shouldFail) {
            LOGGER.error("checkCredit is Failing");
            externalTaskService.handleFailure(externalTask, "Task should fail.", "Because you wanted it.", 0, 1000);
            return;
        }


        Double amountToPay = getDoubleValue(externalTask.getVariable("amountToPay"));
        String userId = externalTask.getVariable("userId");
        if (StringUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("userId or amountToPay can't be empty");
        }
        double amountFromCredit = checkCreditService.getAmountFromCredit(userId);
        LOGGER.info("amountToPay: " + amountToPay + " amountFromCredit: " + amountFromCredit);

        Boolean creditSufficient = true;
        if (amountToPay > amountFromCredit) {
            creditSufficient = false;
        }

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("creditSufficient", creditSufficient);

        // Complete the task
        externalTaskService.complete(externalTask, variables);
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
