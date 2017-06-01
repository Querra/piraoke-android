package de.querra.mobile.piraoke.services;

public class PreferencesServiceFactory implements PreferencesService {

    private static PreferencesService preferencesService;

    public static PreferencesService getInstance(){
        if (preferencesService == null) {
            preferencesService = new PreferencesServiceFactory();
        }
        return preferencesService;
    }

    @Override
    public String getPiraokeIp() {
        return SharedPreferencesServiceFactory.getInstance().retrieveString(PIRAOKE_IP);
    }

    @Override
    public void storePiraokeIp(String ip) {
        SharedPreferencesServiceFactory.getInstance().store(PIRAOKE_IP, ip);
    }
}
