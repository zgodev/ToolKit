package com.zhangyt.network.httputil;

/**
 * 获取有效期限Http请求的回调
 *
 * @author ：licheng@uzoo.com
 */

public interface HttpCallback {

    /**
     * 当成功时，回调方法
     *
     * @param string 消息
     **/
    public void onHttpSuccess(String string);

    /**
     * 当失败时，回调方法
     *
     * @param string 消息
     **/
    public void onHttpError(String string);
}
