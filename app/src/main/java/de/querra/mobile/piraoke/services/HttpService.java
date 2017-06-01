package de.querra.mobile.piraoke.services;

import java.io.IOException;
import java.util.List;

import de.querra.mobile.piraoke.callbacks.OnErrorCallback;
import de.querra.mobile.piraoke.callbacks.OnSuccessCallback;
import de.querra.mobile.piraoke.data_adapters.YTVideo;

public interface HttpService {
    List<YTVideo> searchVideos(String search) throws IOException;
    void connect(String ip, OnSuccessCallback success, OnErrorCallback error);
    void hasConnection(OnSuccessCallback success, OnErrorCallback error);
    void playVideo(String videoId, OnSuccessCallback onSuccess, OnErrorCallback onError);
    void cancelPlayback(String videoId, OnSuccessCallback onSuccess, OnErrorCallback onError);
}
