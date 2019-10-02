package com.kilickaya.svestimator.Util;

import java.text.DecimalFormat;

public class NumericUtils {
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static Double formatDouble(Double value) {
        return Double.valueOf(DECIMAL_FORMAT.format(value));
    }
}
