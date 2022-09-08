package com.ezpzxrp.zerpcraft.XUMM.util;

import java.math.BigInteger;

public class PaymentUtils {

    public static BigInteger convertIntToDrops(double amount) {

        return BigInteger.valueOf((long) (amount * 1000000L));
    }

    public static double convertDropsToDouble(String balance) {

        BigInteger balanceAmount = BigInteger.valueOf(Long.parseLong(balance));
        return balanceAmount.doubleValue() * .000001;
    }
}
