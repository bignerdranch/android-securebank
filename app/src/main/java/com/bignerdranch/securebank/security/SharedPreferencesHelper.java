package com.bignerdranch.securebank.security;

import android.content.SharedPreferences;
import android.util.Base64;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SharedPreferencesHelper {

    private static final String SHARED_PREFERENCES_IV_KEY = "SHARED_PREFERENCES_IV_KEY";
    private static final String SHARED_PREFERENCES_ENCRYPTED_TOKEN = "SHARED_PREFERENCES_ENCRYPTED_TOKEN";
    private static final String SHARED_PREFERENCES_IS_ENROLLED_IN_FINGERPRINT_AUTH = "SHARED_PREFERENCES_IS_ENROLLED_IN_FINGERPRINT_AUTH";
    private static final String SHARED_PREFERENCES_ENABLE_FINGERPRINT_AUTH = "SHARED_PREFERENCES_ENABLE_FINGERPRINT_AUTH";
    private final SharedPreferences sharedPreferences;

    public void setEnableFingerprintAuth(boolean enabled) {
        sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_ENABLE_FINGERPRINT_AUTH, enabled).apply();
    }

    public boolean isFingerprintAuthEnabled() {
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_ENABLE_FINGERPRINT_AUTH, false);
    }

    public void setIsEnrolledInFingerprintAuth() {
        sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_IS_ENROLLED_IN_FINGERPRINT_AUTH, true).apply();
    }

    public boolean isEnrolledInFingerprintAuth() {
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_IS_ENROLLED_IN_FINGERPRINT_AUTH, false);
    }

    public byte[] getIV() {
        String string = sharedPreferences.getString(SHARED_PREFERENCES_IV_KEY, null);
        return Base64.decode(string, Base64.DEFAULT);
    }

    public void saveIV(byte[] iv) {
        String s = Base64.encodeToString(iv, Base64.DEFAULT);
        sharedPreferences.edit().putString(SHARED_PREFERENCES_IV_KEY, s).apply();
    }

    public void saveToken(byte[] encryptedToken) {
        String encoded = Base64.encodeToString(encryptedToken, Base64.NO_WRAP);
        sharedPreferences.edit().putString(SHARED_PREFERENCES_ENCRYPTED_TOKEN, encoded).apply();
    }

    public String getEncryptedToken() {
        return sharedPreferences.getString(SHARED_PREFERENCES_ENCRYPTED_TOKEN, "");
    }

}
