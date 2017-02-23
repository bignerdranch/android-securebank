package com.bignerdranch.securebank;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.CryptoObject;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bignerdranch.securebank.databinding.ActivityLoginBinding;
import com.bignerdranch.securebank.security.CryptoHelper;
import com.bignerdranch.securebank.security.SharedPreferencesHelper;

import javax.crypto.Cipher;
import javax.inject.Inject;

import timber.log.Timber;

import static android.Manifest.permission.USE_FINGERPRINT;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class LoginActivity extends AppCompatActivity {

    private static final String FINGERPRINT_DIALOG_TAG = "FINGERPRINT_DIALOG_TAG";
    static final int FINGERPRINT_AUTH_STAGE_ENROLLMENT = 1;
    static final int FINGERPRINT_AUTH_STAGE_AUTHENTICATION = 2;

    @Inject
    CryptoHelper cryptoHelper;
    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;

    private FingerprintManager fingerprintManager;
    private FingerprintDialogFragment fingerprintDialogFragment;
    private ActivityLoginBinding binding;
    private CancellationSignal cancellationSignal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SecureBankApplication.getAppComponent(this).inject(this);
        fingerprintManager = getSystemService(FingerprintManager.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.fingerprintSignon.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferencesHelper.setEnableFingerprintAuth(isChecked));
        binding.fingerprintSignon.setChecked(sharedPreferencesHelper.isFingerprintAuthEnabled());
        binding.login.setOnClickListener(v -> {
            if (binding.fingerprintSignon.isChecked()) {
                performFingerprintAuthentication();
            } else {
                performRegularAuthentication();
            }
        });
    }

    private void performRegularAuthentication() {
        Toast.makeText(getApplicationContext(),
                "Regular Authentication. Fetch a new token from the webservice",
                Toast.LENGTH_SHORT).show();
    }

    private int getStage() {
        if (sharedPreferencesHelper.isEnrolledInFingerprintAuth() &&
                sharedPreferencesHelper.isFingerprintAuthEnabled()) {
            return FINGERPRINT_AUTH_STAGE_AUTHENTICATION;
        }
        return FINGERPRINT_AUTH_STAGE_ENROLLMENT;
    }

    private void performFingerprintAuthentication() {
        if (ActivityCompat.checkSelfPermission(this, USE_FINGERPRINT) != PERMISSION_GRANTED) {
            return;
        }
        if (!fingerprintManager.isHardwareDetected() || !fingerprintManager.hasEnrolledFingerprints()) {
            Toast.makeText(getApplicationContext(), "Fingerprint Unavailable. Please configure a fingerprint", Toast.LENGTH_SHORT).show();
            return;
        }
        fingerprintDialogFragment = FingerprintDialogFragment.newInstance(getStage());
        fingerprintDialogFragment.show(getFragmentManager(), FINGERPRINT_DIALOG_TAG);
        CryptoObject cryptoObject = configureCryptoObject(getStage());
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                fingerprintDialogFragment.dismiss();
                if (getStage() == FINGERPRINT_AUTH_STAGE_ENROLLMENT) {
                    cryptoHelper.encrypt(result.getCryptoObject(), "hypothetical decrypted token");
                    Toast.makeText(getApplicationContext(), "Fingerprint Registered.", Toast.LENGTH_SHORT).show();
                    sharedPreferencesHelper.setEnableFingerprintAuth(true);
                    sharedPreferencesHelper.setIsEnrolledInFingerprintAuth();
                    performFingerprintAuthentication();
                } else {
                    String decryptedToken = cryptoHelper.decrypt(result.getCryptoObject());
                    Toast.makeText(getApplicationContext(),
                            "Fingerprint Registered. Token decrypted: " + decryptedToken,
                            Toast.LENGTH_SHORT).show();
                    Timber.i("Token received: %s", decryptedToken);
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                }
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //other situations can be handled here like the sensor didnt have a chance
                // to read the image or the operation timed out
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Unrecognized Fingerprint", Toast.LENGTH_SHORT).show();
            }
        }, null);
    }

    private CryptoObject configureCryptoObject(int stage) {
        if (stage == FINGERPRINT_AUTH_STAGE_ENROLLMENT) {
            return cryptoHelper.createCryptoObject(Cipher.ENCRYPT_MODE);
        }
        return cryptoHelper.createCryptoObject(Cipher.DECRYPT_MODE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fingerprintDialogFragment.dismiss();
        cancellationSignal.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getStage() == FINGERPRINT_AUTH_STAGE_AUTHENTICATION) {
            performFingerprintAuthentication();
        }
    }
}
