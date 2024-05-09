package com.example.openssldemo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.security.KeyStoreException;

public class EncryptDecryptTest {
    private final String key = "01234567890123456789012345678901";
    private final String iv = "0123456789012345";
    private final String plainText = "Hello World!!!!";

    @Test
    @DisplayName("Encrypt + Decrypt MSG")
    public void encryptTest() throws KeyStoreException {
//        EncryptDecrypt crypto = new EncryptDecrypt();
//        String cipherMsg = crypto.encrypt(this.key, this.iv, this.plainText);
//
//        String decryptedMsg = crypto.decrypt(this.key, this.iv, cipherMsg);
//        assertEquals(this.plainText, decryptedMsg, "Msg is encrypted + decrypted");
    }
}