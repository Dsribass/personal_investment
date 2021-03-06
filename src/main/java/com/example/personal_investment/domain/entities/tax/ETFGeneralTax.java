package com.example.personal_investment.domain.entities.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ETFGeneralTax implements Tax {

    private final BigDecimal taxAliquot = new BigDecimal("0.15");
    @Override
    public BigDecimal calculateTaxAmount(BigDecimal profit){
        return taxAliquot.multiply(profit).setScale(2, RoundingMode.HALF_EVEN);
    }
}
