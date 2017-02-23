package com.bignerdranch.securebank;

import android.app.Application;
import android.content.Context;

import com.bignerdranch.securebank.security.CryptoHelper;

import javax.inject.Inject;

import lombok.Getter;
import timber.log.Timber;

public class SecureBankApplication extends Application {

    @Getter
    AppComponent appComponent;

    public static SecureBankApplication get(Context context) {
        return (SecureBankApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        initAppComponent();
    }

    private void initAppComponent() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static AppComponent getAppComponent(Context context) {
        return get(context.getApplicationContext()).getAppComponent();
    }

}
