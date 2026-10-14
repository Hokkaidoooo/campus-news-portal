package com.citywy.util;

import javax.servlet.ServletRequest;

public class PublicUtil {
    /**
     * 通过reuquest获取参数
     * @param request
     * @param name 参数名
     * @param defval 默认值
     * @return 返回String类型
     */
    public static final String getParam(ServletRequest request, String name, String defval) {
        String param = request.getParameter(name);
        return (param != null ? param : defval);
    }

    /**
     * 通过reuquest获取参数
     * @param request
     * @param name 参数名
     * @param defval 默认值
     * @return 返回int类型
     */
    public static final int getParam(ServletRequest request, String name, int defval) {
        String param = request.getParameter(name);
        return (param!=null&&!"".equals(param) ? Integer.parseInt(param.trim()) : defval);
    }

    /**
     * 通过reuquest获取参数
     * @param request
     * @param name 参数名
     * @param defval 默认值
     * @return 返回long类型
     */
    public static final long getParam(ServletRequest request, String name, long defval) {
        String param = request.getParameter(name);
        return (param!=null&&!param.equals("") ? Long.parseLong(param) : defval);
    }

    /**
     * 通过reuquest获取参数
     * @param request
     * @param name 参数名
     * @param defval 默认值
     * @return 返回double类型
     */
    public static final double getParam(ServletRequest request, String name, double defval) {
        String param = request.getParameter(name);
        return (param!=null ? Double.parseDouble(param) : defval);
    }


}
