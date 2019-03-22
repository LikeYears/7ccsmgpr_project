package com.oasis.onebox.tool;
/**

 * 计算文本相差度/相似度

 * 返回数字越大，两个字符串相差就越大

 * @author Administrator

 *

 */
public class distance {

    public static void main(String[] args){

        distance dis = new distance();

        String s1="比干缘何落马死，勾践因此可吞吴省长市长共商议，抓好米袋菜篮子饭店是事实上是个 规范的炎热他依然额头";

        String s2="比干缘何落马死勾践因此可吞吴省长市长共商议，抓好米袋分舵是否但是发斯蒂芬斯蒂芬的说法都是菜篮子";

//     System.out.println((s1.length()));

        System.out.println(dis.LD(s2, s1));

    }

    // ****************************

    // Get minimum of three values

    // ****************************

    public int Minimum(int a, int b, int c) {

        int mi;
        mi = a;
        if (b < mi) {
            mi = b;
        }
        if (c < mi) {

            mi = c;

        }
        return mi;
    }
    // *****************************

    // Compute Levenshtein distance

    // *****************************

    public int LD(String s, String t) {

        int d[][]; // matrix

        int n; // length of s

        int m; // length of t

        int i; // iterates through s

        int j; // iterates through t

        char s_i; // ith character of s

        char t_j; // jth character of t

        int cost; // cost

        // Step 1
        n = s.length();

        m = t.length();

        if (n == 0) {

            return m;

        }

        if (m == 0) {

            return n;
        }

        d = new int[n + 1][m + 1];

        // Step 2

        for (i = 0; i <= n; i++) {

            d[i][0] = i;

        }

        for (j = 0; j <= m; j++) {

            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {

                t_j = t.charAt(j - 1);
                // Step 5

                if (s_i == t_j) {

                    cost = 0;

                } else {

                    cost = 1;

                }
                // Step 6

                d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,

                        d[i - 1][j - 1] + cost);
            }
        }
        // Step 7
        return d[n][m];

    }

}