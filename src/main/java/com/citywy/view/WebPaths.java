package com.citywy.view;

public final class WebPaths {
    private WebPaths() { }
    public static String returnQuery(String value) {
        if (value == null || value.isEmpty()) return "";
        if (!value.startsWith("?") || value.contains("\r") || value.contains("\n"))
            throw new IllegalArgumentException("列表返回地址必须是查询参数");
        return value;
    }
}
