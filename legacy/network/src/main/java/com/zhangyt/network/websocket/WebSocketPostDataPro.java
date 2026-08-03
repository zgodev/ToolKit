package com.zhangyt.network.websocket;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import com.zhangyt.utils.LogManager;
import com.zhangyt.utils.NetCheck;
import com.zhangyt.utils.ThreadPoolUtil;
import com.zhangyt.utils.TuringCode;
import com.zhangyt.network.httputil.UrlConstants;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebSocketPostDataPro {
    private String TAG = "WebSocketPostData";

    private static final int MIN_POST_DATA_SIZE = 1280;
    private Context mContext = null;
    private ClientListener mListener = null;
    private OkHttpWebSocket mWebSocketClient = null;
    private volatile boolean mAvailable = false;
    private AtomicBoolean mShouldStop = new AtomicBoolean(false);
    private AtomicBoolean mShouldClose = new AtomicBoolean(false);
    private final ByteArrayOutputStream mPostStream = new ByteArrayOutputStream(1024 * 20);
    private String url;
    public WebSocketPostDataPro(String tag) {
        TAG = "WebSocketPostDataPro" + "-->" + tag;
    }

    public WebSocketPostDataPro(String tag, String url) {
        TAG = "WebSocketPostDataPro" + "-->" + tag;
//        LogManager.e(TAG,"url----"+url);
        this.url = url;
    }
    public void start(Context context, ClientListener listener) {
        mContext = context;
        mListener = listener;

        LogManager.i(TAG, "start websocket");
        mShouldStop.set(false);
        mShouldClose.set(false);
        synchronized (mPostStream) {
            mPostStream.reset();
        }
        synchronized (this) {
            if (NetCheck.isNetActive(mContext)) {
                if (!isAvailable()) {
                    doConnect();
                } else {
                    if (mListener != null) {
                        mListener.onConnected();
                    }
                }
            } else {
                LogManager.e(TAG, "connect fail cause no net !");
                if (mListener != null) {
                    mListener.onError(TuringCode.WEBSOCKET_NO_NET, "Connect fail cause no net !");
                }
            }
        }
    }
    public void sendPostData(String data, boolean isEnd) {
        if (mWebSocketClient != null) {
            LogManager.i(TAG, " isAvailable(): " + isAvailable());
            if (!isAvailable()) {
                if (isEnd) {
                    LogManager.e(TAG, "ASR end and Socket close ");
                    if (mListener != null) {
                        mListener.onError(TuringCode.WEBSOCKET_EXCEPTION, "ASR end and Socket close !");
                    }
                } else {
//                    new Thread().start();
                    ThreadPoolUtil.getNetExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (mListener != null) {
                                mListener.onResend();
                            }
                        }
                    });
                }
            } else {
                mWebSocketClient.sendData(data);
            }
        } else {
            LogManager.i(TAG, "sendPostData mWebSocketClient is null !");
            if (mListener != null) {
                mListener.onResend();
            }
        }
    }

    public void sendPostData(String data) {
        sendPostData(data, false);
    }

    public void sendPostData(byte[] data) {
        try {
            synchronized (this) {
                if (mWebSocketClient != null && data != null) {
                    mWebSocketClient.sendData(data);
                }
            }
        } catch (Exception e) {
            LogManager.e(TAG, "sendPostData error",e);
        }
    }

    public void stop() {
        synchronized (this) {
            LogManager.i(TAG, "stop postData: " + mAvailable);
            mAvailable = false;
        }
    }

    public void close() {
        synchronized (this) {
            LogManager.i(TAG, "close postData: " + mShouldClose.get());
            if (mShouldClose.get()) {
                return;
            }
            mAvailable = false;
            mShouldClose.set(true);
        }
        doDisConnect();
    }

    private boolean checkClosed() {
        boolean closed = mShouldClose.compareAndSet(true, false);
        if (closed) {
            doDisConnect();
        }
        return closed;
    }

    public boolean doConnect() {
        synchronized (this) {
            mAvailable = false;
            OkHttpWebSocket socketClient = getWebSocketClient();
            if (socketClient == null) {
                return false;
            } else {
                socketClient.simpleConnect();
                return true;
            }
        }
    }

    private void doDisConnect() {
        synchronized (this) {
            if (mWebSocketClient != null && mWebSocketClient.isConnected()) {
                mWebSocketClient.simpleClose();
                mWebSocketClient = null;
            }
            mWebSocketClient = null;
        }
    }

    private boolean isAvailable() {
        synchronized (this) {
            if (mWebSocketClient == null || !mWebSocketClient.isConnected() || mShouldClose.get()) {
                mAvailable = false;
            }
            return mAvailable;
        }
    }

    private void processMessage(String message) {
//        LogManager.i(TAG, "message： " + message);
        Gson gson = new Gson();
//        BaseResp baseResp = gson.fromJson(message, BaseResp.class);
        if (mListener != null) {
//            LogManager.e(TAG, "message code:" + baseResp.getCode() + " globalId:" + baseResp.getGlobalId());
//            mListener.onResult(baseResp.getCode(), message, baseResp);
            mListener.onResult(1, message);
        }

    }

    private void sendTerminatorInfo() {
        LogManager.i(TAG, "sendTerminatorInfo");
//        mWebSocketClient.sendData(new RecognizedRequest.Builder(RecognizedRequest.SpeechState.STATE_END).build().toJson());
    }

    private void threadSleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            LogManager.e(TAG,"threadSleep error",e);
        }
    }

    private OkHttpWebSocket getWebSocketClient() {
        synchronized (this) {
            if (mWebSocketClient == null || !mWebSocketClient.isConnected()) {
                LogManager.i(TAG, "new mWebSocketClient");
                mWebSocketClient = generateWebSocketClient();
            }
            return mWebSocketClient;
        }
    }

    private OkHttpWebSocket generateWebSocketClient() {
        try {
            if (TextUtils.isEmpty(url)) {
                url = UrlConstants.getProductUrl();
//                LogManager.e(TAG,"url--->"+url);
            }
//            URI uri = new URI(url);
            return new OkHttpWebSocket(url, new OkHttpWebSocket.Listener() {
                @Override
                public void onOpen() {
                    LogManager.i(TAG, checkClosed() + "============onOpen==========");
                    mAvailable = true;
                    if (!checkClosed()) {
                        if (mListener != null) {
                            mListener.onConnected();
                        } else {
                            LogManager.i(TAG, "mListener is null!");
                        }
                    } else {
                        LogManager.i(TAG, "checkClosed() is false!");
                    }
                }

                @Override
                public void onMessage(String message) {
                    LogManager.i(TAG, checkClosed() + "============onMessage String=========");
                    if (!checkClosed()) {
                        processMessage(message);
                    }
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
//                    LogManager.d(TAG, checkClosed() + "============onMessage ByteBuffer =========");
                    if (mListener != null) {
                        mListener.onResult(bytes);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    LogManager.i(TAG, checkClosed() + "============onClose==========");
                    synchronized (this) {
                        mAvailable = false;
                    }
                    if (mListener != null) {
                        mListener.onClosed(code, reason, remote);
                    }
                }

                @Override
                public void onClosing(int code, String reason) {
                    LogManager.i(TAG, checkClosed() + "============onClosing==========");
                }

                @Override
                public void onError(Exception e) {
//                    e.printStackTrace();
                    LogManager.i(TAG, checkClosed() + "============onError==========" + e.toString());
                    synchronized (this) {
                        mAvailable = false;
                    }
                    if (mListener != null) {
                        mListener.onError(TuringCode.WEBSOCKET_EXCEPTION, e.toString());
                    }
                }
            });
        } catch (Exception e) {
            LogManager.e(TAG, "generate webSocket client error",e);
            if (mListener != null) {
                mListener.onError(TuringCode.WEBSOCKET_EXCEPTION, e.toString());
            }
            return null;
        }
    }

    public void clearListener() {
        mListener = null;
    }
}
