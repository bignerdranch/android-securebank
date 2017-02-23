package com.bignerdranch.securebank.security;

import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CryptoHelper {

    private KeyStoreHelper keyStoreHelper;
    private SharedPreferencesHelper sharedPreferencesHelper;

    public Cipher getCipher() {
        try {
            //Cipher Transformation Configured as: AES/CBC/PKCS7Padding
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public FingerprintManager.CryptoObject createCryptoObject(int mode) {
        Cipher cipher = getCipher();
        if (mode == Cipher.ENCRYPT_MODE) {
            initEncryptCipher(cipher);
        } else {
            initDecryptCipher(cipher);
        }
        return new FingerprintManager.CryptoObject(cipher);
    }

    public void initEncryptCipher(Cipher cipher) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyStoreHelper.getSecretKey());
            sharedPreferencesHelper.saveIV(cipher.getIV());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void initDecryptCipher(Cipher cipher) {
        try {
            byte[] iv = sharedPreferencesHelper.getIV();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keyStoreHelper.getSecretKey(), ivParameterSpec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void encrypt(FingerprintManager.CryptoObject cryptoObject, String authTokenFromServer) {
        try {
            Cipher cipher = cryptoObject.getCipher();
            byte[] bytes = cipher.doFinal(authTokenFromServer.getBytes());
            sharedPreferencesHelper.saveToken(bytes);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public String decrypt(FingerprintManager.CryptoObject cryptoObject) {
        try {
            Cipher cipher = cryptoObject.getCipher();
            String textToDecrypt = sharedPreferencesHelper.getEncryptedToken();
            ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(textToDecrypt, Base64.DEFAULT));
            CipherInputStream cipherInputStream = new CipherInputStream(is, cipher);
            BufferedReader reader = new BufferedReader(new InputStreamReader(cipherInputStream));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
