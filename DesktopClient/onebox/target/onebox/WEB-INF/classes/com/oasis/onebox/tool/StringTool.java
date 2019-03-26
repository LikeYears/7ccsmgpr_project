package com.oasis.onebox.tool;

import java.util.UUID;

public class StringTool {

    public static boolean isNullOrEmpty(String a)
    {
        return a == null || "".equals(a);
    }

    public static String getUUID()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
