package com.zhangyt.network.websocket;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.zhangyt.utils.LogManager;
import com.zhangyt.network.httputil.HttpsUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkHttpWebSocket implements IOkHttpWebSocket {
    private String TAG = OkHttpWebSocket.class.getCanonicalName();
    private WebSocket webSocket;
    private Listener mListener = null;
    private volatile boolean isConnected = false;
    private String url;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    public OkHttpWebSocket(String url, Listener listener) {
        mListener = listener;
        this.url = url;
        mHandlerThread = new HandlerThread("webSocket-thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void simpleConnect() {
        synchronized (OkHttpWebSocket.class) {
            if (!isConnected&&url!=null){
                connectWebSocket(url);
            }
        }
    }

    @Override
    public void simpleClose() {
        synchronized (OkHttpWebSocket.class) {
            webSocket.cancel();
            mHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }
    }

    @Override
    public void sendData(byte[] data) {
        synchronized (OkHttpWebSocket.class) {
            if (!isConnected||webSocket==null) {
                LogManager.e(TAG, "webSocket is not connected!");
                return;
            }
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int dataLen = data.length;
                    int oneNum = 128 * 1024;
                    if (dataLen > oneNum) {
                        int num = dataLen / oneNum;
                        int index = 0;
                        for (int i = 0; i < num; i++) {
                            byte[] temp = Arrays.copyOfRange(data, index, index + oneNum);
                            index = index + oneNum;
                            webSocket.send(new ByteString(temp));
                            LogManager.e(TAG, "send imageData tep:" + i + " len:" + oneNum);
                        }
                        int endNum = dataLen % oneNum;
                        if (endNum > 0) {
                            byte[] end = Arrays.copyOfRange(data, index, dataLen);
                            LogManager.e(TAG, "send imageData tep:end ,len:" + endNum);
                            webSocket.send(new ByteString(end));
                        }
                    } else {
                        webSocket.send(new ByteString(data));  // 一次发完
                    }
                } catch (Exception e) {
                    mListener.onError(e);
                }
            }
        });
    }

    @Override
    public void sendData(String data) {
        synchronized (OkHttpWebSocket.class) {
            if (!isConnected||webSocket==null) {
                LogManager.e(TAG, "webSocket is not connected!");
                return;
            }
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                webSocket.send(data);
            }
        });
    }

    @Override
    public boolean isConnected() {
        synchronized (OkHttpWebSocket.class) {
            return isConnected;
        }
    }

    private void connectWebSocket(String url){
        OkHttpClient clientOkHttp = new OkHttpClient.Builder()
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(), new HttpsUtils.SafeTrustManager())
                .followRedirects(false)
                .followSslRedirects(false)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
//                        return hostname.equals(UrlUtil.getWsHost(null));
                        //FIXME 需要判断hostName
                        return true;
                    }
                })
                .build();
        //构造request对象
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Sec-WebSocket-Protocol", "janus-protocol")
                .build();
        webSocket = clientOkHttp.newWebSocket(request, new OkWebSocketListener());
    }

    class OkWebSocketListener extends WebSocketListener {
        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosed(webSocket, code, reason);
            LogManager.e(TAG,"WebSocket onClosed");
            isConnected = false;
            mListener.onClose(code,reason,true);
            mHandlerThread.quit();
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosing(webSocket, code, reason);
            LogManager.e(TAG,"WebSocket onClosing");
            isConnected = false;
            mListener.onClosing(code,reason);
            mHandlerThread.quit();
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            LogManager.e(TAG,"WebSocket onFailure");
            isConnected = false;
            mListener.onError(new Exception(t));
            mHandlerThread.quit();
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
//            LogManager.e(TAG,"onMessage text:"+text);
            mListener.onMessage(text);
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            mListener.onMessage(bytes.asByteBuffer());
//            LogManager.e(TAG,"onMessage text:"+new String(bytes.asByteBuffer().array()));
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            LogManager.e(TAG,"WebSocket onOpen");
            isConnected = true;
            mListener.onOpen();
        }
    }
    public interface Listener {
        void onOpen();

        void onMessage(String message);

        void onMessage(ByteBuffer bytes);

        void onClose(int code, String reason, boolean remote);

        void onClosing(int code, String reason);

        void onError(Exception e);
    }
}
