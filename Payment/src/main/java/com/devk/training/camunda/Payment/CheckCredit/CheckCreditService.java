package com.devk.training.camunda.Payment.CheckCredit;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class CheckCreditService {

    public double getAmountFromCredit(String userid) {
        double random = ThreadLocalRandom.current().nextDouble(0, 99999999);
        return random;
    }
}
