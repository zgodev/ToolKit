package com.zhangyt.network.websocket;

public interface IOkHttpWebSocket {
    void simpleConnect();

    void simpleClose();

    void sendData(final byte[] data);

    void sendData(final String data);

    boolean isConnected();
}
