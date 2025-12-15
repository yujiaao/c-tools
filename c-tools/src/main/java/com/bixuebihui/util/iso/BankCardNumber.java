package com.bixuebihui.util.iso;

/**
 * @author xwx
 */
public class BankCardNumber {

    public static void main(String[] args) {
        String card = "";
        System.out.println("      card: " + card);
        System.out.println("check aaaaaaaaa code: " + getBankCardCheckCode(card));
        System.out.println("   card id: " + card + getBankCardCheckCode(card));
        System.out.println("   card boolean: " + checkBankCard(card));
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return 如果符合校验规则返回true
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return 根据卡号算出的校验码
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0 || !nonCheckCodeCardId.matches("\\d+")) {
            throw new IllegalArgumentException("Bank card code must be number!");
        }

        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }
}
