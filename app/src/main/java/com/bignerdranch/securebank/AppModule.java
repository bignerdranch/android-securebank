package com.bignerdranch.securebank;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bignerdranch.securebank.security.CryptoHelper;
import com.bignerdranch.securebank.security.KeyStoreHelper;
import com.bignerdranch.securebank.security.SharedPreferencesHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application application;

    AppModule(Application application) {
        this.application = application;
    }

    @Provides
    CryptoHelper cryptoHelper(KeyStoreHelper keyStoreHelper, SharedPreferencesHelper sharedPreferencesHelper) {
        return new CryptoHelper(keyStoreHelper, sharedPreferencesHelper);
    }

    @Provides
    SharedPreferencesHelper sharedPreferencesHelper() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        return new SharedPreferencesHelper(sharedPreferences);
    }

    @Provides
    KeyStoreHelper keyStoreHelper() {
        return new KeyStoreHelper();
    }

}
