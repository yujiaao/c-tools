public class DebugTest {
    public static void main(String[] args) {
        // 创建测试SQL
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO users VALUES(?");
        sb.append(",?".repeat(59));
        sb.append(")");
        String originalSql = sb.toString();
        
        System.out.println("Original SQL: " + originalSql);
        System.out.println("SQL length: " + originalSql.length());
        
        // 计算问号数量
        int questionMarkCount = 0;
        for (int i = 0; i < originalSql.length(); i++) {
            if (originalSql.charAt(i) == '?') {
                questionMarkCount++;
            }
        }
        System.out.println("Question mark count: " + questionMarkCount);
        
        // 测试reduce方法
        String result = reduce(originalSql);
        System.out.println("Result SQL: " + result);
        System.out.println("Contains omit marker: " + result.contains("...omit"));
    }
    
    static String reduce(String sql) {
        //对形如 ?,?,? 过多（50个以上）个问号的占位符，替换成 ?,...omit XXX ,?
        if (sql.length() > 500) {
            // 查找连续的逗号间隔的问号模式
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < sql.length()) {
                // 寻找连续的问号序列开始位置
                int start = sql.indexOf('?', i);
                if (start == -1) {
                    // 没有找到问号，添加剩余部分并退出
                    sb.append(sql.substring(i));
                    break;
                }

                // 添加开始位置之前的内容
                sb.append(sql, i, start + 1);

                // 检查是否是连续的逗号间隔的问号
                int j = start + 1;
                int questionMarkCount = 1;
                boolean isConsecutive = true;

                while (j < sql.length()) {
                    if (j < sql.length() - 1 && sql.charAt(j) == ',' && sql.charAt(j + 1) == '?') {
                        questionMarkCount++;
                        j += 2;
                    } else {
                        isConsecutive = false;
                        break;
                    }
                }

                // 如果是连续的逗号间隔的问号且数量超过50个，进行替换
                if (isConsecutive && questionMarkCount > 50) {
                    int omittedCount = questionMarkCount - 2;
                    sb.append(",...omit ").append(omittedCount).append(" ,?");
                    i = j;
                } else {
                    // 不是连续的问号序列或数量不超过50，从下一个位置继续查找
                    i = start + 1;
                }
            }
            return sb.toString();
        }
        return sql;
    }
}
