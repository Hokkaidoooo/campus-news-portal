package com.citywy.util;

import com.citywy.domain.User;
import com.citywy.service.UserService;

/** 显式创建初始账号，密码只通过环境传入并由同一用户服务哈希。 */
public final class AccountAdmin {
    private AccountAdmin() { }
    public static void main(String[] args) {
        if (args.length != 1 || !"--create-admin".equals(args[0]))
            throw new IllegalArgumentException("需要显式 --create-admin 参数");
        String name = System.getenv("NEWS_ADMIN_NAME"), password = System.getenv("NEWS_ADMIN_PASSWORD");
        if (name == null || name.trim().isEmpty() || password == null || password.isEmpty())
            throw new IllegalArgumentException("需要 NEWS_ADMIN_NAME 和 NEWS_ADMIN_PASSWORD");
        User user = new User();
        user.setName(name); user.setUname(name); user.setPassword(password);
        user.setRole("管理员"); user.setValid("有效");
        try {
            if (!new UserService().addUser(user)) throw new IllegalStateException("创建账号未成功");
            System.out.println("管理员账号已创建，数据库仅保存密码哈希。");
        } finally { DBConnectionPool.shutdown(); }
    }
}
