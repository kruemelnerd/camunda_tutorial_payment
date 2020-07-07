package com.devk.training.camunda.Payment.CheckCredit;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Service
public class CheckCreditService {

    public double getAmountFromCredit(String userid) {
        double leftLimit = 1D;
        double rightLimit = 100000D;
        double generatedDouble = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);

        BigDecimal bd = BigDecimal.valueOf(generatedDouble);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}
