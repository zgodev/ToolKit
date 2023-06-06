package com.zhangyt.network.websocket;

import com.zhangyt.utils.LogManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @Author yihuapeng
 * @Date 2020/3/25 12:47
 **/
public class WebSocketManager {
    private final static String TAG = WebSocketManager.class.getSimpleName();
    private final static int MAX_NUM = 5;       // 最大重连数
    private final static int MILLIS = 5000;     // 重连间隔时间，毫秒
    private volatile static WebSocketManager manager;

    private OkHttpClient client;
    private Request request;
    private IReceiveMessage receiveMessage;
    private WebSocket mWebSocket;

    private boolean isConnect = false;
    private int connectNum = 0;

    public WebSocketManager(String url, IReceiveMessage message) {
        init(url, message);
    }


    public void init(String url, IReceiveMessage message) {
        client = new OkHttpClient.Builder()
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder().url(url).build();
        receiveMessage = message;
//        connect();
    }

    /**
     * 连接
     */
    public void connect() {
        if (isConnect()) {
            LogManager.i(TAG, "web socket connected");
            return;
        }
        client.newWebSocket(request, createListener());
    }

    /**
     * 重连
     */
    public void reconnect() {
        if (connectNum <= MAX_NUM) {
            try {
                Thread.sleep(MILLIS);
                connect();
                connectNum++;
            } catch (InterruptedException e) {
                LogManager.e(TAG,"reconnect error!",e);
            }
        } else {
            LogManager.i(TAG, "reconnect over " + MAX_NUM + ",please check url or network");
        }
    }

    /**
     * 是否连接
     */
    public boolean isConnect() {
        return mWebSocket != null && isConnect;
    }

    /**
     * 发送消息
     *
     * @param text 字符串
     * @return boolean
     */
    public boolean sendMessage(String text) {
        if (!isConnect()) return false;
        return mWebSocket.send(text);
    }

    /**
     * 发送消息
     *
     * @param byteString 字符集
     * @return boolean
     */
    public boolean sendMessage(ByteString byteString) {
        if (!isConnect()) return false;
        return mWebSocket.send(byteString);
    }
    public boolean sendMessage(byte[] bytes) {
        if (!isConnect()) return false;
        return mWebSocket.send(ByteString.of(bytes));
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (isConnect()) {
            mWebSocket.cancel();
            mWebSocket.close(1001, "客户端主动关闭连接");
        }
    }

    private WebSocketListener createListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LogManager.i(TAG, "open() ");
                mWebSocket = webSocket;
                isConnect = response.code() == 101;
                if (!isConnect) {
                    reconnect();
                } else {
                    LogManager.i(TAG, "connect success.");
                    if (receiveMessage != null) {
                        receiveMessage.onConnectSuccess();
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if (receiveMessage != null) {
                    receiveMessage.onMessage(text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (receiveMessage != null) {
                    receiveMessage.onMessage(bytes.base64());
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (receiveMessage != null) {
                    receiveMessage.onCloseing(code, reason);
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (receiveMessage != null) {
                    receiveMessage.onClose(code, reason);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                isConnect = false;
                if (response != null) {
                    LogManager.i(TAG, "connect failed：" + response.message());
                }
                LogManager.i(TAG, "connect failed throwable：" + t.getMessage());
                if(t.getMessage() != null){
                    if(t.getMessage().equals("Connection reset")){
                        receiveMessage.onClose(1006, t.getMessage());
                    }else{
                        receiveMessage.onError(t.getMessage());
                    }
                }else{
                    receiveMessage.onClose(1006, t.getMessage());
                }
            }
        };
    }
}
