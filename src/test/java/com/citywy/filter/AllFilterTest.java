package com.citywy.filter;

import com.citywy.domain.User;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;

public class AllFilterTest {
    @Test public void publicRequestsDoNotReadAccountsOrCreateSessions() throws Exception {
        Exchange request = new Exchange(null);
        request.path = "/api/article";
        request.run(filter(id -> { throw new AssertionError("公开请求不能查询账号"); }));
        assertTrue(request.passed);
        assertFalse(request.sessionRequested);
    }

    @Test public void anonymousRequestsAreDeniedBeforeLookup() throws Exception {
        Exchange request = new Exchange(null);
        request.run(filter(id -> { throw new AssertionError("匿名请求没有账号 ID"); }));
        assertEquals(401, request.status);
        assertFalse(request.passed);
        assertEquals("no-store", request.headers.get("Cache-Control"));
    }

    @Test public void editorCanEditContentButCannotUseAnyUserManagementRoute() throws Exception {
        User editor = user("用户", "有效");
        AllFilter filter = filter(id -> editor);
        for (String path : new String[]{"/servlets/news/modify.htm", "/servlets/notice/list.htm", "/admin/ajax/pic.htm"}) {
            Exchange request = new Exchange(editor);
            request.path = path;
            request.run(filter);
            assertTrue(path, request.passed);
            assertEquals(false, request.attributes.get("canManageUsers"));
        }
        for (String path : new String[]{"/servlets/user/list.htm", "/servlets/user/modify.htm", "/servlets/user/drop.htm",
                "/admin/user/user_load.jsp", "/admin/user/user_list.jsp"}) {
            Exchange request = new Exchange(editor);
            request.path = path;
            request.run(filter);
            assertEquals(path, 403, request.status);
            assertFalse(request.passed);
        }
    }

    @Test public void managerCanManageUsersAndSessionReceivesCurrentIdentity() throws Exception {
        User current = user("管理员", "有效");
        current.setName("改名后的账号");
        Exchange request = new Exchange(user("管理员", "有效"));
        request.path = "/servlets/user/load.htm";
        request.run(filter(id -> current));
        assertTrue(request.passed);
        assertSame(current, request.sessionAttributes.get("loginuser"));
        assertEquals(current.getName(), request.sessionAttributes.get("username"));
        assertEquals(true, request.attributes.get("canManageUsers"));
    }

    @Test public void staleAdminSessionCannotKeepPermissionsAfterDemotion() throws Exception {
        Exchange request = new Exchange(user("管理员", "有效"));
        request.path = "/servlets/user/modify.htm";
        request.run(filter(id -> user("用户", "有效")));
        assertEquals(403, request.status);
        assertFalse(request.passed);
    }

    @Test public void disabledOrDeletedAccountInvalidatesExistingSession() throws Exception {
        for (User current : new User[]{user("管理员", "无效"), null}) {
            Exchange request = new Exchange(user("管理员", "有效"));
            request.run(filter(id -> current));
            assertEquals(401, request.status);
            assertTrue(request.invalidated);
            assertFalse(request.passed);
        }
    }

    @Test public void unknownRoleIsNotGrantedEditingPermissions() throws Exception {
        Exchange request = new Exchange(user("未知角色", "有效"));
        request.run(filter(id -> user("未知角色", "有效")));
        assertEquals(403, request.status);
        assertFalse(request.passed);
    }

    @Test public void databaseFailureNeverReusesCachedPermissions() throws Exception {
        Exchange request = new Exchange(user("管理员", "有效"));
        IllegalStateException failure = new IllegalStateException("数据库不可用");
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> request.run(filter(id -> { throw failure; }))));
        assertFalse(request.passed);
    }

    @Test public void rolesAreConfigurableWithoutDisablingAuthorization() throws Exception {
        AllFilter filter = new AllFilter(id -> user("账号维护", "有效"));
        filter.init(config("账号维护", "编辑"));
        Exchange request = new Exchange(user("账号维护", "有效"));
        request.path = "/servlets/user/list.htm";
        request.run(filter);
        assertTrue(request.passed);
        assertEquals(java.util.Arrays.asList("编辑", "账号维护"), request.attributes.get("backendRoles"));
        assertThrows(IllegalArgumentException.class, () -> filter.init(config("重复", "重复")));
        assertThrows(IllegalArgumentException.class, () -> filter.init(config(null, "编辑")));
    }

    private static AllFilter filter(IntFunction<User> lookup) {
        AllFilter filter = new AllFilter(lookup);
        filter.init(config("管理员", "用户"));
        return filter;
    }

    private static FilterConfig config(String manager, String editor) {
        return proxy(FilterConfig.class, (object, method, args) -> {
            if (method.getName().equals("getInitParameter")) return args[0].equals("user-management-role") ? manager : editor;
            throw new AssertionError("Unexpected FilterConfig method: " + method.getName());
        });
    }

    private static User user(String role, String valid) {
        User user = new User();
        user.setId(7); user.setName("account"); user.setRole(role); user.setValid(valid);
        return user;
    }

    @SuppressWarnings("unchecked") private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    private static final class Exchange {
        final Map<String, Object> sessionAttributes = new HashMap<>();
        final Map<String, Object> attributes = new HashMap<>();
        final Map<String, String> headers = new HashMap<>();
        final HttpSession session;
        String path = "/admin/index.jsp";
        int status = 200;
        boolean passed, invalidated, sessionRequested;

        Exchange(User user) {
            if (user != null) sessionAttributes.put("loginuser", user);
            session = user == null ? null : proxy(HttpSession.class, (object, method, args) -> {
                switch (method.getName()) {
                    case "getAttribute": return sessionAttributes.get(args[0]);
                    case "setAttribute": sessionAttributes.put((String) args[0], args[1]); return null;
                    case "invalidate": invalidated = true; sessionAttributes.clear(); return null;
                    default: throw new AssertionError("Unexpected session method: " + method.getName());
                }
            });
        }

        void run(AllFilter filter) throws Exception {
            HttpServletRequest request = proxy(HttpServletRequest.class, (object, method, args) -> {
                switch (method.getName()) {
                    case "setCharacterEncoding": return null;
                    case "getServletPath": return path;
                    case "getPathInfo": case "getHeader": return null;
                    case "getSession": assertEquals(Boolean.FALSE, args[0]); sessionRequested = true; return session;
                    case "setAttribute": attributes.put((String) args[0], args[1]); return null;
                    default: throw new AssertionError("Unexpected request method: " + method.getName());
                }
            });
            HttpServletResponse response = proxy(HttpServletResponse.class, (object, method, args) -> {
                switch (method.getName()) {
                    case "setCharacterEncoding": return null;
                    case "setHeader": headers.put((String) args[0], (String) args[1]); return null;
                    case "sendError": status = (Integer) args[0]; return null;
                    default: throw new AssertionError("Unexpected response method: " + method.getName());
                }
            });
            filter.doFilter(request, response, (input, output) -> passed = true);
        }
    }
}
