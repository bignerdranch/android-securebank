package com.bignerdranch.securebank;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class
        }
)
interface AppComponent {
    void inject(LoginActivity activity);
}
