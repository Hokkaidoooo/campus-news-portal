package com.citywy.util;

import com.citywy.domain.User;
import com.citywy.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserUtil {
    /**
     * 获取当前登录的用户对象
     * @return
     */
    public static User getLoginUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("loginuser");
        return u;
    }

    /**
     * 根据用户名获取用户实例
     * @param name
     * @return
     */
    public static User getUserByName(String name) {
        UserService userService = new UserService();
        User user = userService.getUserByName(name);
        return user;
    }
}
