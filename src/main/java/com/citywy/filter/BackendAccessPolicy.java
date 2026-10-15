package com.citywy.filter;

import com.citywy.domain.User;
import java.util.Arrays;
import java.util.List;

/** 角色标签只从 AccessControl 的部署配置读取；界面与请求授权共用这份策略。 */
final class BackendAccessPolicy {
    private final String managerRole;
    private final String editorRole;

    BackendAccessPolicy(String managerRole, String editorRole) {
        this.managerRole = requireRole(managerRole);
        this.editorRole = requireRole(editorRole);
        if (managerRole.equals(editorRole)) throw new IllegalArgumentException("管理员和编辑角色必须不同");
    }

    boolean canManageUsers(User user) { return managerRole.equals(user.getRole()); }
    boolean canEditContent(User user) { return canManageUsers(user) || editorRole.equals(user.getRole()); }
    List<String> roles() { return java.util.Collections.unmodifiableList(Arrays.asList(editorRole, managerRole)); }

    static boolean isUserManagement(String path) {
        return path.equals("/servlets/user") || path.startsWith("/servlets/user/")
                || path.equals("/admin/user") || path.startsWith("/admin/user/");
    }

    private static String requireRole(String role) {
        if (role == null || role.trim().isEmpty() || !role.equals(role.trim())) {
            throw new IllegalArgumentException("必须配置非空的角色标签");
        }
        return role;
    }
}
