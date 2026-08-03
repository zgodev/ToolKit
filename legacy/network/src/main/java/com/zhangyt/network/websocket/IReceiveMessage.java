package com.zhangyt.network.websocket;

/**
 * @Author yihuapeng
 * @Date 2020/3/25 12:49
 **/
public interface IReceiveMessage {
    void onConnectSuccess();// 连接成功

    void onConnectFailed();// 连接失败

    void onClose(int code, String reason); // 关闭

    void onCloseing(int code, String reason); // 关闭

    void onMessage(String text);

    void onError(String message);
}
