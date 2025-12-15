package com.bixuebihui.util.gb;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xwx
 */
public class HKShenfenzheng {
    /**
     * 英文字母（老年人通常是A或者B，成年人多数是之后的英文字母，如D, E之类；
     * 现在的青少年[20岁左右]多数是Z, 现在小孩多数Y，内地移民不久的多是V)
     * <p>
     * 如果您是青少年（17岁或以上吧），
     * 就应该是Z开头的，之后的第一个数字就要看您出生的年份了。
     * 1982年的，多数是3、1985年的，多数是6、1988年的，可能是9，如此类推。
     * 过了1988年，很多人的头一个英文字母都变成了Y，所以头一个数目字都变成了1，甚至0。
     * 之后的数字都是乱编。但要注意的是最后一个括号数字。那时一个很复杂的问题。
     * <p>
     * 香港身份证号码的最后一个字是括号内的数字，
     * 是根据一条方程式算出来的，假设号码头六个字是 Z687485 ,
     * 则要先把英文字母Z转化为数字，即26，乘以8（如果是Y，则是25，如此类推），
     * 然后把各个数字从左至右分别乘以7、6、5、4、3、2。
     * <p>
     * 按例：
     * <p>
     * Z（亦即26）×8+ 6×7 + 8×6 + 7×5 + 4×4 + 8×3 +5×2
     * <p>
     * 算出来的结果应该是383，那么就将这个总和除以11，
     * 如果是整除，括号内的数字就等于0；有余数的话，就将11减去该余数，
     * 就得出括号内的数字，按例那个就是2；如果11减去该余数后得出10，
     * 该括号内就会写上A字。
     * <p>
     * 这样，就得出一个完整的香港特区身份证号码了。这个号码送给您，Z687485(2)。
     */
    public static boolean isValid(String id) {
        if (StringUtils.isBlank(id) || id.length() < 8 || id.length() > 10) {
            return false;
        }


        id = id.replaceAll("\\(", "").replaceAll("\\)", "");
        id = id.toUpperCase();
        int dig = (id.charAt(0) - 64) * 8
                + Byte.parseByte(id.charAt(1) + "") * 7
                + Byte.parseByte(id.charAt(2) + "") * 6
                + Byte.parseByte(id.charAt(3) + "") * 5
                + Byte.parseByte(id.charAt(4) + "") * 4
                + Byte.parseByte(id.charAt(5) + "") * 3
                + Byte.parseByte(id.charAt(6) + "") * 2;
        dig = dig % 11;
        dig = 11 - dig;

        if (dig == 10) {
            return id.charAt(7) == 'A';
        }
        return dig == Byte.parseByte(id.charAt(7) + "");
    }
}
