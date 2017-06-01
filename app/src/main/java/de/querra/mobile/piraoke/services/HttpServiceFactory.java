package de.querra.mobile.piraoke.services;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import de.querra.mobile.piraoke.callbacks.OnErrorCallback;
import de.querra.mobile.piraoke.callbacks.OnSuccessCallback;
import de.querra.mobile.piraoke.data_adapters.YTVideo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpServiceFactory implements HttpService {

    private final static String YOUTUBE_BASE_URL = "https://www.youtube.com";
    private final static String SEARCH_PREFIX = "/results?q=";
    private final static String SEARCH_DELIMITER = " ";
    private final static String CONCATENATION = "+";
    private final static String KAROKE_SUFFIX = "karaoke";
    private final static String DURATION_DELIMITER = ":";

    private final static String URL_PREFIX = "https://";
    private final static String PIRAOKE_PORT = ":8080";
    private final static String PIRAOKE_BASE_URL = "/piraoke/default/";
    private final static String PI_PLAYER_ENDPOINT = "player";
    private final static String PI_CANCEL_ENDPOINT = "cancel";
    private final static String PI_CONNECT_ENDPOINT = "connect";

    private static HttpService httpService;
    private final OkHttpClient okHttpClient;

    private HttpServiceFactory() {
        this.okHttpClient = getUnsafeOkHttpClient();
    }

    public static HttpService getInstance() {
        if (httpService == null) {
            httpService = new HttpServiceFactory();
        }
        return httpService;
    }

    @Override
    public List<YTVideo> searchVideos(String search) throws IOException {
        List<YTVideo> ytVideos = new ArrayList<>();
        String processedSearch = StringUtils.replace(search, SEARCH_DELIMITER, CONCATENATION);
        String searchUrl = String.format(Locale.getDefault(), "%s%s%s%s%s", YOUTUBE_BASE_URL, SEARCH_PREFIX, processedSearch, CONCATENATION, KAROKE_SUFFIX);
        Document document = Jsoup.connect(searchUrl).get();
        Elements links = document.getElementsByClass("yt-lockup-content");
        for (Element element : links) {
            Element titleLinkContainer = element.getElementsByClass("yt-lockup-title").first();
            Element titleLink = titleLinkContainer.getElementsByClass("yt-uix-tile-link").first();
            String title = titleLink.attr("title");
            String videoUrlSuffix = titleLink.attr("href");
            Element imageLink = document.getElementsByAttributeValue("href", videoUrlSuffix).first();
            Element videoThumbDiv = imageLink.getElementsByClass("video-thumb").first();
            Element videoThumbSpan = videoThumbDiv.getElementsByClass("yt-thumb-simple").first();
            Element videoThumbImage = videoThumbSpan.getElementsByAttribute("data-ytimg").first();
            String imageUrl = videoThumbImage.attr("src");
            if (imageUrl.contains(".gif")) {
                imageUrl = videoThumbImage.attr("data-thumb");
            }
            Element titleDescription = titleLinkContainer.getElementsByClass("accessible-description").first();
            String duration = titleDescription.text().split(DURATION_DELIMITER, 2)[1];

            YTVideo ytVideo = new YTVideo();
            ytVideo.setTitle(title);
            ytVideo.setDuration(duration);
            ytVideo.setImageUrl(imageUrl);
            ytVideo.setVideoId(videoUrlSuffix.split("=")[1]);
            ytVideos.add(ytVideo);
        }

        return ytVideos;
    }


    @Override
    public void connect(String ip, final OnSuccessCallback onSuccess, final OnErrorCallback onError) {
        Request request = new Request.Builder().url(getConnectUrl(ip)).build();

        this.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                onError.onError(exception);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onError.onError(new IOException("Unexpected code " + response));
                } else {
                    onSuccess.onSuccess();
                }
            }
        });
    }

    @Override
    public void hasConnection(OnSuccessCallback success, OnErrorCallback error) {
        connect(PreferencesServiceFactory.getInstance().getPiraokeIp(), success, error);
    }

    @Override
    public void playVideo(String videoId, final OnSuccessCallback onSuccess, final OnErrorCallback onError) {
        Request request = new Request.Builder().url(getPlayVideoUrl(videoId)).build();

        this.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                onError.onError(exception);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onError.onError(new IOException("Unexpected code " + response));
                } else {
                    onSuccess.onSuccess();
                }
            }
        });
    }


    @Override
    public void cancelPlayback(String videoId, final OnSuccessCallback onSuccess, final OnErrorCallback onError) {
        Request request = new Request.Builder().url(getCancelVideoUrl(videoId)).build();

        this.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException exception) {
                onError.onError(exception);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onError.onError(new IOException("Unexpected code " + response));
                } else {
                    onSuccess.onSuccess();
                }
            }
        });
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.connectTimeout(30, TimeUnit.SECONDS).build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getBaseUrl(String piraokeIp) {
        return String.format("%s%s%s%s", URL_PREFIX, piraokeIp, PIRAOKE_PORT, PIRAOKE_BASE_URL);
    }

    private String getEndpointUrl(String endpoint, String piraokeIp) {
        return String.format("%s%s", getBaseUrl(piraokeIp), endpoint);
    }

    private String getPlayerUrl() {
        String piraokeIp = PreferencesServiceFactory.getInstance().getPiraokeIp();
        return getEndpointUrl(PI_PLAYER_ENDPOINT, piraokeIp);
    }

    private String getCancelUrl() {
        String piraokeIp = PreferencesServiceFactory.getInstance().getPiraokeIp();
        return getEndpointUrl(PI_CANCEL_ENDPOINT, piraokeIp);
    }

    private String getConnectUrl(String piraokIp) {
        return getEndpointUrl(PI_CONNECT_ENDPOINT, piraokIp);
    }

    private String getPlayVideoUrl(String videoId) {
        return getVideoIdUrl(videoId, getPlayerUrl());
    }

    private String getCancelVideoUrl(String videoId) {
        return getVideoIdUrl(videoId, getCancelUrl());
    }

    private String getVideoIdUrl(String videoId, String endpointUrl) {
        return String.format(Locale.getDefault(), "%s?video_id=%s", endpointUrl, videoId);
    }
}
