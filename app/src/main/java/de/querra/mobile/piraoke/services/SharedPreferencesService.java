package de.querra.mobile.piraoke.services;

public interface SharedPreferencesService {

    String SHARED_PREFERENCES = "piraoke_shared_preferences";

    void store(String key, String value);
    String retrieveString(String key);
}
