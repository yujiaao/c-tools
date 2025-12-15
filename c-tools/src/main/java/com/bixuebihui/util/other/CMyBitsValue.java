
package com.bixuebihui.util.other;

import java.math.BigInteger;


/**
 * @author xwx
 */
public class CMyBitsValue
        implements Cloneable {

    protected long value;

    public CMyBitsValue() {
        value = 0L;
        value = 0L;
    }

    public CMyBitsValue(long lValue) {
        value = 0L;
        setValue(lValue);
    }

    public CMyBitsValue(String sValue) {
        value = 0L;
        setValue(sValue);
    }

    public CMyBitsValue(CMyBitsValue myBitsValue) {
        value = 0L;
        copy(myBitsValue);
    }

    public static boolean getBitOfInt(int value, int index) {
        if (index < 0 || index > 31) {
            return false;
        } else {
            return BigInteger.valueOf(value).testBit(index);
        }
    }

    public static boolean getBit(long value, int index) {
        if (index < 0 || index > 63) {
            return false;
        } else {
            return BigInteger.valueOf(value).testBit(index);
        }
    }

    public static long setBit(long value, int index, boolean bValue) {
        if (index < 0 || index > 63) {
            return value;
        }
        BigInteger bigInt = BigInteger.valueOf(value);
        if (bValue) {
            bigInt = bigInt.setBit(index);
        } else {
            bigInt = bigInt.clearBit(index);
        }
        return bigInt.longValue();
    }

    protected static int setBitOfInt(int value, int index, boolean bValue) {
        if (index < 0 || index > 31) {
            return value;
        } else {
            return (int) setBit(value, index, bValue);
        }
    }

    public static void main(String[] args) {
        CMyBitsValue myBitsValue = new CMyBitsValue(0L);
        System.out.println(myBitsValue.toString(10));
        myBitsValue.setBit(31, true);
        System.out.println(myBitsValue.toString());
        myBitsValue.setBit(1, true);
        myBitsValue.setBit(2, false);
        myBitsValue.setBit(3, true);
        myBitsValue.setBit(10, true);
        myBitsValue.setBit(9, false);
        myBitsValue.setBit(8, true);
        myBitsValue.setBit(7, true);
        System.out.println(myBitsValue.toString());
        System.out.println("10=" + myBitsValue.getBit(10));
        System.out.println("1=" + myBitsValue.getBit(1));
        System.out.println("2=" + myBitsValue.getBit(2));
        int nLen = 64;
        myBitsValue.setValue(0L);
        for (int i = 0; i < nLen; i++) {
            myBitsValue.setBit(i, true);
        }

        for (int i = nLen - 1; i >= 0; i--) {
            System.out.print(i % 10 != 0 || i <= 0 ? String.valueOf(i % 10) : " ");
        }

        System.out.print("\n");
        System.out.println(myBitsValue.toString(nLen));
        for (int i = nLen - 1; i >= 0; i--) {
            System.out.print(myBitsValue.getBit(i) ? "1" : "0");
        }

        System.out.println("\n");
        System.out.println(CMyString.expandStr(Long.toBinaryString(0x7fffffffffffffffL), 64, '0', true) + " : MAX=" + 0x7fffffffffffffffL);
        System.out.println(Long.toBinaryString(0x8000000000000000L) + " : MIN=" + 0x8000000000000000L);
        System.out.println("\n========= Test for BigInteger ==========");
        BigInteger bigInt = BigInteger.valueOf(0L);
        bigInt = bigInt.setBit(31).setBit(63);
        System.out.println(bigInt.toString(2));
        long lValue = bigInt.longValue();
        System.out.println(Long.toBinaryString(lValue));
        bigInt = BigInteger.valueOf(lValue);
        System.out.println("\n");
        for (int i = 63; i >= 0; i--) {
            System.out.print(bigInt.testBit(i) ? "1" : "0");
        }

        int nValue = 0;
        nValue = setBitOfInt(nValue, 31, true);
        System.out.println("\n int = " + nValue);
        for (int i = 0; i < 32; i++) {
            nValue = setBitOfInt(nValue, i, (i & 1) == 1);
        }

        nValue = setBitOfInt(nValue, 31, false);
        for (int i = 31; i >= 0; i--) {
            System.out.print(getBitOfInt(nValue, i) ? "1" : "0");
        }

        System.out.println("\n");
        System.out.println(Long.toBinaryString(nValue));
        String sValue = "10101000100011110001";
        myBitsValue.setValue(sValue);
        System.out.println("src = " + sValue);
        System.out.println("dst = " + myBitsValue.toString());
        myBitsValue.or(2L);
        System.out.println("or  = " + myBitsValue.toString());
        myBitsValue.and(5L);
        System.out.println("and = " + myBitsValue.toString());
    }

    public void copy(CMyBitsValue bitsValue) {
        setValue(bitsValue.getValue());
    }

    @Override
    public Object clone() {
        return new CMyBitsValue(this);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long nValue) {
        value = nValue;
    }

    public void setValue(String sValue) {
        if (sValue == null) {
            return;
        }
        BigInteger bigInt = BigInteger.valueOf(0L);
        int nLen = sValue.length();
        int nBitPos = nLen - 1;
        for (int i = 0; i < nLen; i++) {
            if (sValue.charAt(i) == '1') {
                bigInt = bigInt.setBit(nBitPos);
            }
            nBitPos--;
        }

        value = bigInt.longValue();
    }

    public int getValueAsInt() {
        return (int) value;
    }

    public boolean getBit(int index) {
        return getBit(value, index);
    }

    public CMyBitsValue setBit(int index, boolean bValue) {
        value = setBit(value, index, bValue);
        return this;
    }

    public void and(CMyBitsValue bitsValue) {
        if (bitsValue == null) {
        } else {
            and(bitsValue.getValue());
        }
    }

    public void and(long value) {
        BigInteger bigInt = BigInteger.valueOf(this.value);
        bigInt = bigInt.and(BigInteger.valueOf(value));
        this.value = bigInt.longValue();
    }

    public void or(CMyBitsValue myBitsValue) {
        if (myBitsValue == null) {
        } else {
            or(myBitsValue.getValue());
        }
    }

    public void or(long value) {
        BigInteger bigInt = BigInteger.valueOf(this.value);
        bigInt = bigInt.or(BigInteger.valueOf(value));
        this.value = bigInt.longValue();
    }

    public int getRealLength() {
        return BigInteger.valueOf(value).bitLength();
    }

    @Override
    public String toString() {
        return toString(getRealLength());
    }

    public String toString(int length) {
        StringBuffer strBuffer = new StringBuffer(length);
        String sValue = Long.toBinaryString(value);
        if (length > getRealLength()) {
            sValue = CMyString.expandStr(sValue, length, '0', true);
        }
        for (int i = length - 1; i > -1; i--) {
            strBuffer.append(sValue.charAt(i));
        }

        return strBuffer.toString();
    }
}
