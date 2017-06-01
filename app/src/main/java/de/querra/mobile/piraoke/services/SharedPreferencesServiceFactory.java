package de.querra.mobile.piraoke.services;

import android.content.Context;
import android.content.SharedPreferences;

import de.querra.mobile.piraoke.PiraokeApplication;

public class SharedPreferencesServiceFactory implements SharedPreferencesService {

    private static SharedPreferencesService sharedPreferencesService;
    private final SharedPreferences sharedPreferences;

    public static SharedPreferencesService getInstance() {
        if (sharedPreferencesService == null) {
            sharedPreferencesService = new SharedPreferencesServiceFactory();
        }
        return sharedPreferencesService;
    }

    private SharedPreferencesServiceFactory() {
        this.sharedPreferences = PiraokeApplication.CONTEXT.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void store(String key, String value) {
        this.sharedPreferences.edit().putString(key, value).apply();
    }

    @Override
    public String retrieveString(String key) {
        return this.sharedPreferences.getString(key, null);
    }
}
