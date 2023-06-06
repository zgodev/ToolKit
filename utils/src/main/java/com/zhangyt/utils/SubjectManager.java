package com.zhangyt.utils;

import java.util.ArrayList;

public class SubjectManager {
    private static ArrayList<IObserverListener> observerList = new ArrayList<>();
    private volatile static SubjectManager mManager;
    private String mMsg;
    public interface IObserverListener {
        void updateMsg(String msg);
        void updateSuc();
    }
    public static synchronized SubjectManager getInstance() {
        if (mManager == null) {
            mManager = new SubjectManager();
        }
        return mManager;
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        this.mMsg = msg;
        notifyAlls();
    }

    public void sendSuc(){
        for (IObserverListener observer : observerList) {
            if (observer != null) {
                observer.updateSuc();
            }
        }
    }
    /**
     * 注册事件
     *
     * @param observer
     */
    public void registrationObserver(IObserverListener observer) {
        if (observerList != null && observer != null) {
            observerList.add(observer);
        }
//        notifyAlls();
    }

    /**
     * 删除事件
     *
     * @param observer
     */
    public void unregistrationObserver(IObserverListener observer) {
        if (observerList != null && observer != null) {
            observerList.remove(observer);
        }
    }

    /**
     * 分发消息
     */
    private void notifyAlls() {
        for (IObserverListener observer : observerList) {
            if (observer != null) {
                observer.updateMsg(mMsg);
            }
        }
    }
}
