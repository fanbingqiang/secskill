package com.bq.util;


import org.springframework.stereotype.Component;

@Component
public class shortCodeUtil {

    // 62 进制字符集：0-9 a-z A-Z
    private static final String CHARS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    /**
     * 雪花 ID → 62 进制短码
     */
    public String encode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int mod = (int) (id % BASE);
            sb.append(CHARS.charAt(mod));
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    /**
     * 62 进制短码 → 雪花 ID（反查用）
     */
    public long decode(String code) {
        long id = 0;
        for (char c : code.toCharArray()) {
            id = id * BASE + CHARS.indexOf(c);
        }
        return id;
    }
}