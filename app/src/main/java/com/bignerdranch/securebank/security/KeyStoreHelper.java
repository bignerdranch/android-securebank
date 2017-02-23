package com.bignerdranch.securebank.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyStoreHelper {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String DEFAULT_KEY = "default_key";
    private KeyStore keystore;

    public KeyStoreHelper() {
        loadKeystore();
        if (getSecretKey() == null) {
            createSecretKey();
        }
    }

    private void loadKeystore() {
        try {
            keystore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keystore.load(null);
        } catch (IOException | NoSuchAlgorithmException |
                CertificateException | KeyStoreException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void createSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES,
                            ANDROID_KEY_STORE);
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec
                    .Builder(DEFAULT_KEY, KeyProperties.PURPOSE_ENCRYPT |
                    KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    SecretKey getSecretKey() {
        try {
            return (SecretKey) keystore.getKey(DEFAULT_KEY, null);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
