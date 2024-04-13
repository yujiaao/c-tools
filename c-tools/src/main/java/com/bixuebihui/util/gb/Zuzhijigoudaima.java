package com.bixuebihui.util.gb;

import com.bixuebihui.util.other.CMyException;


/**
 * 组织机构的录入是有一定规则的，规则如下：
 * 组织机构代码是每一个机关、社会团体、企事业单位在全国范围内唯一的、始终不变的法定代码标识。
 * 最新使用的组织机构代码在1997年颁布实施，由8位数字（或大写拉丁字母）本体代码和1位数字（或大写拉丁字母）校验码组成。本体代码采用系列（即分区段）顺序编码方法。校验码按下列公式计算：
 * 8
 * C9 = 11 - MOD ( ∑Ci * Wi ，11) … (2)
 * i=1
 * 其中：MOD —— 表示求余函数；
 * i —— 表示代码字符从左到右位置序号；
 * Ci —— 表示第i位置上的代码字符的值，采用附录A“代码字符集”所列字符；
 * C9 —— 表示校验码；
 * Wi —— 表示第i位置上的加权因子，其数值如下表：
 * i 1 2 3 4 5 6 7 8
 * Wi 3 7 9 10 5 8 4 2
 * 当MOD函数值为1（即 C9 = 10）时，校验码用字母X表示。
 *
 * @author xwx
 */
public class Zuzhijigoudaima {

    static final String[] Wi = {"3", "7", "9", "10", "5", "8", "4", "2"};
    static final char[] ai = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'};
    static final byte[] bi = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
            31, 32, 33, 34, 35};

    private byte GetAi(char S) {
        byte Result = 100;
        for (int I = 0; I <= 35; I++) {
            if (S == ai[I]) {
                Result = bi[I];
                break;
            }
        }
        return Result;
    }

    /**
     * 获得校验码
     *
     * @param dwdm
     * @return 单位校验码
     * @throws CMyException
     */
    public char dwdmjym(String dwdm) throws CMyException {
        char ReChar;
        int Sum, ModResult;
        byte S;
        boolean Result = true;
        Sum = 0;
        for (int J = 0; J < dwdm.length(); J++) {
            S = GetAi(dwdm.charAt(J));
            if (S == 100) {
                throw new CMyException(50001, "非法字符");
            }
            Sum = Sum + ((int) (S)) * Integer.parseInt(Wi[J]);
        }


        ModResult = Sum % 11;
        if (ModResult == 1) {
            ReChar = 'X';
        } else if (ModResult == 0) {
            ReChar = '0';
        } else {
            ReChar = ((11 - ModResult) + "").charAt(0);
        }
        return ReChar;
    }

    /**
     * 单位代码校验
     *
     * @return 校验通过返回 TRUE
     * @throws CMyException
     */
    public boolean dwdmjy(String dwdm) throws CMyException {
        if (dwdm.indexOf("-") > 0) {
            dwdm = dwdm.replaceAll("-", "");
        }

        if (dwdm.length() != 9) {
            return false;
        }

        return dwdm.charAt(8) == dwdmjym(dwdm.substring(0, 8));
    }
}
