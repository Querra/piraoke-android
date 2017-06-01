package de.querra.mobile.piraoke.services;

public interface PreferencesService {
    String PIRAOKE_IP = "preference_piraoke_ip";

    String getPiraokeIp();
    void storePiraokeIp(String ip);
}