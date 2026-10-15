package com.citywy.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordHashesTest {
    @Test
    public void usesSaltAndRejectsWrongOrTruncatedPasswords() {
        String first = PasswordHashes.hash("测试密码-one");
        String second = PasswordHashes.hash("测试密码-one");
        assertNotEquals(first, second);
        assertTrue(PasswordHashes.verify("测试密码-one", first));
        assertFalse(PasswordHashes.verify("测试密码-two", first));
        String boundary = String.join("", java.util.Collections.nCopies(24, "汉"));
        String hash = PasswordHashes.hash(boundary);
        assertTrue(PasswordHashes.verify(boundary, hash));
        assertFalse(PasswordHashes.verify(boundary + "x", hash));
        assertThrows(IllegalArgumentException.class, () -> PasswordHashes.hash(boundary + "x"));
    }

    @Test
    public void plaintextAndCorruptEncodingsFailExplicitly() {
        assertThrows(IllegalStateException.class, () -> PasswordHashes.verify("legacy", "legacy"));
        assertThrows(IllegalStateException.class, () -> PasswordHashes.verify("x", "$2a$12$broken"));
        assertThrows(IllegalStateException.class, () -> PasswordHashes.verify("x", null));
    }
}
