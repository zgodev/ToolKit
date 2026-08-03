package com.zhangyt.network.websocket;



import java.nio.ByteBuffer;

public interface ClientListener {
    /**
     * 可以传输二进制数据
     */
    void onConnected();

    /**
     * 连接断开回调
     */
    void onClosed(int code, String reason, boolean remote);

    /**
     * 连接中间过程回调（出错、识别结果等）
     *
     * @param code   see{@TuringCode}
     * @param result see{@BaseResp}
     */
//    void onResult(int code, String result, BaseResp baseResp);
    void onResult(int code, String result);

    void onResult(ByteBuffer bytes);

    void onError(int code, String msg);

    void onResend();
}
