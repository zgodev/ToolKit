package com.zhangyt.network.httputil;

import androidx.annotation.NonNull;


import com.zhangyt.utils.LogManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 * 获取userId的http方法
 *
 * @author：licheng@uzoo.com
 */

public class HttpHelper {
    private final static String TAG = HttpHelper.class.getSimpleName();
    private static HttpHelper httpHelper;

    public static HttpHelper getInstance() {
        if (httpHelper == null) {
            synchronized (HttpHelper.class) {
                if (httpHelper == null)
                    httpHelper = new HttpHelper();
            }
        }
        return httpHelper;
    }

    public HttpHelper() {
    }

    public void getSplitWord(String data, String deviceId, String apikey, String secret, HttpCallback callback) {
        LogManager.i(TAG, "getSplitWord");
        HashMap<String, Object> secretBefore = new HashMap<>();
        secretBefore.put("deviceId", deviceId);
        secretBefore.put("text", data);
//        JSONObject object = secretAfterV3(secret, apikey, secretBefore);
        //FIXME 此处需要填写url 以及请求参数
        requestPostOkHttps("URL_SPLIT_WORD", "requestStr", callback);
    }

    public void requestPostOkHttps(final String urlString, final String jsonString, final HttpCallback callback) {
//        LogManager.e(TAG,"requestPost:"+jsonString);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384)
                .build();
        builder.connectTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        return chain.proceed(chain.request()
                                .newBuilder().addHeader("Content-Type", "application/json")
                                .addHeader("Connection", "Keep-Alive")
                                .addHeader("Charset", "UTF-8").build());
                    }
                })
                .connectionSpecs(Collections.singletonList(spec))
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(null, null, null), new HttpsUtils.SafeTrustManager())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        //FIXME 此处需要校验 hostName
//                        return (hostname.equals("hostName"));
                        return true;
                    }
                });
        OkHttpClient okHttpClient = builder.build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonString);
        Request request = new Request.Builder()
                .url(urlString)
                .post(body).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogManager.e(TAG,"okhttp request error!",e);
                callback.onHttpError(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.onHttpSuccess(Objects.requireNonNull(response.body()).string());

            }
        });
    }

}
