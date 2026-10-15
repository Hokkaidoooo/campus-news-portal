package com.citywy.util;

import org.mindrot.jbcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class PasswordHashes {
    private static final int COST = 12;
    private static final Pattern BCRYPT = Pattern.compile("\\$2a\\$(0[4-9]|1[0-6])\\$[./A-Za-z0-9]{53}");
    private PasswordHashes() { }

    public static String hash(String password) {
        if (!validInput(password)) throw new IllegalArgumentException("Password must contain 1 to 72 UTF-8 bytes");
        return BCrypt.hashpw(password, BCrypt.gensalt(COST));
    }

    public static boolean verify(String password, String storedHash) {
        requireSupportedHash(storedHash);
        if (!validInput(password)) return false;
        try {
            return BCrypt.checkpw(password, storedHash);
        } catch (IllegalArgumentException failure) {
            throw new IllegalStateException("Stored password hash is corrupt; run an explicit password reset", failure);
        }
    }

    public static void requireSupportedHash(String hash) {
        if (hash == null || !BCRYPT.matcher(hash).matches()) {
            throw new IllegalStateException("Stored password is plaintext, corrupt or unsupported; run PasswordMigration or reset the password");
        }
    }

    public static boolean looksEncoded(String value) { return value != null && value.startsWith("$2"); }

    private static boolean validInput(String password) {
        return password != null && !password.isEmpty() && password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
