package com.zhangyt.utils;

import android.util.SparseArray;

public final class TuringCode {

    // "https://api.turingos.cn" 请求成功的code
    public static final int API_HTTP_SUC_CODE = 200;

    //  "https://iot-ai.turingapi.com" 请求成功的code
    public static final int IOT_HTTP_SUC_CODE = 0;

    public static final int IOT_NOT_NEW_VERSION_CODE = 10404;

    /**
     * 网络未连接
     */
    public static final int NETWORK_ERROR = 100001;
    /**
     * 鉴权错误
     */
    public static final int DEVICEID_ERROR = 100002;
    /**
     * SDK权限过期了
     */
    public static final int AUTHORITY_HAS_EXPIRED = 100003;


    /**
     * 参数为空
     */
    public static final int CLIENT_PARAM_NULL = 200002;
    /**
     * 输入参数错误
     */
    public static final int CLIENT_PARAM_ERROR = 200003;
    /**
     * 请求任务溢出，请求池限制200
     */
    public static final int CLIENT_REQUEST_OVER = 200004;

    /**
     * 单轮请求20s限制超时
     */
    public static final int CLIENT_TIMEOUT = 200005;

    public static final int CLIENT_OVER = 200006;


    /**
     * ASR stream没有初始化
     */
    public static final int CLIENT_ASR_NOINIT = 200007;

    public static final int CLIENT_ASR_INIT_TIMEOUT = 200008;
    public static final int CLIENT_ASR_END_TIMEOUT = 200009;

    // vad error
    public static final int VAD_INITIALIZE_FAILED = 200010;
    public static final int VAD_READ_ONE_FRAME_FAILED = 200011;
    public static final int VAD_READ_NO_DATA = 200012;
    public static final int VOICE_DATA_TIMEOUT = 200013;
    public static final int VOICE_DATA_ERROR = 200014;

    public static final int CLIENT_BOOK_OVERFLOW = 200015;
    public static final int CLIENT_SEND_TIMEOUT = 200016;

    public static final int CLIENT_BOOK_INIT_TIMEOUT = 200017;
    public static final int CLIENT_BOOK_END_TIMEOUT = 200018;
    public static final int CLIENT_BOOK_NOSTATE = 200019;
    // opus codec error
    public static final int OPUS_CODEC_INITIALIZE_FAILED = 200020;
    public static final int OPUS_CODEC_PROCESS_FAILED = 200021;
    public static final int CLIENT_FINGER_END_TIMEOUT = 200022;
    public static final int CLIENT_PERMISSION_REFUSE = 200023;//没有MANAGE_EXTERNAL_STORAGE权限


    // voiceprogess
    public static final int VOICE_PROGESS_FAILED = 200030;

    public static final int CLIENT_RECOGNIZE_ERROR = 200031;//识别失败
    public static final int CLIENT_CONFIG_ERROR = 200032;//配置文件异常
    public static final int CLIENT_DPI_NOT_MATCH = 200033;//图片分辨率与云端配置的分辨率不匹配
    public static final int BOOK_STATE_RESET_FAILED = 200034;//绘本状态重置失败
    public static final int HTTP_REQUEST_FAILED = 200035;//网络请求失败
    public static final int DATA_PARSER_ERROR = 200036;//数据解析异常
    public static final int CLIENT_CAMERA_ERROR = 200037;//摄像头配置异常
    public static final int CLIENT_CAMERA_OPEN_ERROR = 200038;//摄像头打开失败
    public static final int CLIENT_FILE_WRITE_ERROR = 200039;//文件写入失败
    public static final int VAD_WRITE_DATA_FAILED = 200040;//vad 写入数据失败

    public static final int CLIENT_OTHER_ERROR = 200099;//其他错误

    // init error
    public static final int V3_INIT_ERROR = 200100;
    public static final int V3_ACCESS_USERINFO_ERROR = 200101;

    //离线能力权限，需在后台开通离线能力
    public static final int PERMISSION_NO_OFFLINE_DICT = 200105;
    public static final int PERMISSION_NO_OFFLINE_VAD = 200106;
    public static final int PERMISSION_NO_OFFLINE_TTS_TRANS = 200107;
    public static final int PERMISSION_NO_OFFLINE_HAND = 200108;

    // 360 文档裁切
    public static final int CAMERA_NO_PERMISSION = 200200; // 未开启相机权限
    public static final int CAMERA_NOT_SUPPORT_CAMERA2 = 200201; // 当前设备不支持 Camera 2.0
    public static final int CAMERA_CONFIG_FAIL = 200202; //相机配置失败
    public static final int DOC_FAIL_DOWNLOAD_AUTHFILE = 200203; //鉴权文件下载失败
    public static final int PERMISSION_NO_OFFLINE_DOC = 200204; //请检查是否授权文档检测能力！
    public static final int DOC_NOT_FIND = 200205; //未检测到文档
    public static final int DOC_FIND_SUC = 200206; // 检测到，正常情况
    public static final int DOC_LACK_ANGLE = 200207; // 缺角
    public static final int DOC_LACK_EDGE = 200208; // 缺边

    public static final int MATH_NO_ABILITY = 200300; // 未匹配到口算批改能力

    // 坐姿检测 200400~200411 对应坐姿检测sdk中错误码，故顺序任意不能变动。
    public static final int PERMISSION_NO_OFFLINE_POSTURE = 200400; //请检查是否授权坐姿检测检测能力!
    public static final int POSTURE_INPUT_PARAMETER_ERR = 200401; // 输入参数错误
    public static final int POSTURE_REPEATED_CALL = 200402; // 重复调用
    public static final int POSTURE_USERINFO_NOT_EXIST = 200403; // 用户信息不存在
    public static final int POSTURE_NUMBER_OF_USER_REGI_USED_UP = 200404; // 用户注册数量已用完
    public static final int POSTURE_DEVICEID_EXCEEDED_REGISTERED_USERS = 200405; // 获取的设备信息数量远超用户注册数量
    public static final int POSTURE_FAILED_TO_INSERT_OR_REGISTER = 200406; // 新设备插入或注册失败
    public static final int POSTURE_NET_SERVICE_INPUT_PARAMETER_EMPTY = 200407; // 网络服务输入参数为空
    public static final int POSTURE_SOME_INPUT_PARAMETER_NULL = 200408; // 某些输入参数为空
    public static final int POSTURE_UNKNOWN_SERVER_EXCEPTION = 200409; // Unknown Server Exception(异常)
    public static final int POSTURE_FAILED_TO_CONNECT_SERVER = 200410; // 连接服务器失败
    public static final int POSTURE_USER_DEVICEID_EXCEEDS_80BYTES = 200411; // 用户设备唯一信息超过80字节

    public static final int COMPOSITION_NOT_INIT = 200500; // 作文批改没有初始化
    public static final int COMPOSITION_NOT_OFFLINE = 200501; // 请检查是否授权作文批改能力!



    public static final int USER_OPERATE_VALID_ASR_PAUSE_FINGER = 300000;
    public static final int FINGER_OCR_INIT_FAIL = 300001;


    // recorder error
    public static final int RECORDER_READ_ONE_FRAME_FAILED = 200040;
    public static final int RECORDER_INITIALIZE_FAILED = 200041;
    public static final int DATA_ERROE = 200042;

    // 口算批改-服务端错误码
    public static final int CODE_ABILITY_MATH = 1000669;
    public static final int CODE_OPERATESTATE_ANSWER_SUC = 1120;
    public static final int CODE_OPERATESTATE_ANSWER_ERR = 1121;
    public static final int CODE_OPERATESTATE_NOIMAGE = 1035;
    public static final int CODE_OPERATESTATE_INNER_ERR = 1036;
    public static final int CODE_OPERATESTATE_FAILT = 1037;


    // websocket error
    // 200该轮交互正常结束
    public static final int WEBSOCKET_200 = 200;
    public static final int WEBSOCKET_210 = 210;
    public static final int WEBSOCKET_220 = 220;
    //TTS失败
    public static final int WEBSOCKET_230 = 230;
    //ASR识别结果无内容
    public static final int WEBSOCKET_240 = 240;
    public static final int WEBSOCKET_ASR_200 = 200;
    public static final int WEBSOCKET_ASR_201 = 201;
    public static final int WEBSOCKET_TIMEOUT = 4102;

    public static final int WEBSOCKET_SERVER_ERROR = -41;
    public static final int WEBSOCKET_NO_NET = -42;
    public static final int WEBSOCKET_EXCEPTION = -43;
    public static final int WEBSOCKET_CONNECT_ERROR = -44;//webSocket 连接失败
    public static final int WEBSOCKET_DISCONNECT_ERROR = -45;//webSocket 异常断开，非超时断开

    public static final int SUCCESSS = 1;


    //iot
    /**
     * IOT初始化错误
     */
    public static final int IOT_INIT_ERRCODE = 400001;
    /**
     * IOT MQTT连接错误
     */
    public static final int IOT_CONNECT_ERRCODE = 400002;
    /**
     * IOT MQTT连接异常
     */
    public static final int IOT_CONNECT_EXCEPTION = 400003;
    /**
     * IOT MQTT订阅话题异常
     */
    public static final int IOT_SUBSCRIBE_EXCEPTION = 400004;
    /**
     * Bind 获取绑定码为空
     */
    public static final int BIND_CODE_NULL = 400005;
    /**
     * IOT 与服务器失去连接
     */
    public static final int IOT_CONNECTION_LOST = 400006;

    static SparseArray<String> codeMsg = new SparseArray<>();

    static {
        codeMsg.put(NETWORK_ERROR, "网络异常!");
        codeMsg.put(DEVICEID_ERROR, "获取Device ID异常！");
        codeMsg.put(AUTHORITY_HAS_EXPIRED, "Apikey过期");
        codeMsg.put(CLIENT_PARAM_NULL, "输入参数为空！");
        codeMsg.put(CLIENT_PARAM_ERROR, "输入参数错误！");
        codeMsg.put(CLIENT_REQUEST_OVER, "请求任务栈溢出，默认限制200个任务");
        codeMsg.put(CLIENT_TIMEOUT, "单轮请求20s限制超时!");
        codeMsg.put(CLIENT_OVER, "请求动作太过频繁！");
        codeMsg.put(CLIENT_ASR_NOINIT, "ASR已经结束或者通道没有初始化，请停止传入数据！");
        codeMsg.put(CLIENT_ASR_INIT_TIMEOUT, "ASR初始化时网络请求超时!");
        codeMsg.put(CLIENT_ASR_END_TIMEOUT, "ASR结束时网络请求超时!");
        codeMsg.put(CLIENT_BOOK_INIT_TIMEOUT, "绘本初始化时网络请求超时!");
        codeMsg.put(CLIENT_BOOK_END_TIMEOUT, "绘本结束时网络请求超时!");
        codeMsg.put(CLIENT_FINGER_END_TIMEOUT, "指尖查词时网络请求超时!");
        codeMsg.put(CLIENT_BOOK_OVERFLOW, "绘本请求频繁！");
        codeMsg.put(CLIENT_BOOK_NOSTATE, "当前不在绘本识别状态！");
        codeMsg.put(CLIENT_CONFIG_ERROR, "配置文件异常！");
        codeMsg.put(CLIENT_DPI_NOT_MATCH, "图片分辨率与云端配置的分辨率不匹配");
        codeMsg.put(BOOK_STATE_RESET_FAILED, "绘本状态重置失败");
        codeMsg.put(CLIENT_RECOGNIZE_ERROR, "识别失败，请重试！");
        codeMsg.put(CLIENT_SEND_TIMEOUT, "网络请求超时!");
        codeMsg.put(VOICE_DATA_TIMEOUT, "长时间未发送音频数据 ！");
        codeMsg.put(VOICE_DATA_ERROR, "音频数据为空或者音频数据不合法,音频数据长度不能大于1280个byte,而且要求必须是320的整数倍!");
        codeMsg.put(DATA_ERROE, "数据解析错误！");
        codeMsg.put(CLIENT_PERMISSION_REFUSE, "读写权限异常");
        codeMsg.put(HTTP_REQUEST_FAILED, "网络请求失败");
        codeMsg.put(DATA_PARSER_ERROR, "数据解析异常");
        codeMsg.put(CLIENT_FILE_WRITE_ERROR, "文件写入失败");
        codeMsg.put(CLIENT_CAMERA_ERROR, "摄像头异常");
        codeMsg.put(CLIENT_CAMERA_OPEN_ERROR, "摄像头打开失败");

        codeMsg.put(VOICE_PROGESS_FAILED, "btn_to_voice initialize error !");
        codeMsg.put(VAD_INITIALIZE_FAILED, "vad initialize error !");
        codeMsg.put(VAD_READ_ONE_FRAME_FAILED, "vad process data error !");
        codeMsg.put(VAD_READ_NO_DATA, "Voice detection results have no data !");
        codeMsg.put(VAD_WRITE_DATA_FAILED, "vad 写入数据失败 !");


        codeMsg.put(V3_INIT_ERROR, "V3 init error!");
        codeMsg.put(V3_ACCESS_USERINFO_ERROR, "V3 access userinfo error!");

        codeMsg.put(PERMISSION_NO_OFFLINE_DICT, "请检查是否授权离线词典能力！");

        codeMsg.put(USER_OPERATE_VALID_ASR_PAUSE_FINGER, "用户操作非法, 必须在查词过程中使用");
        codeMsg.put(FINGER_OCR_INIT_FAIL, "离线手指模型未成功初始化");


        codeMsg.put(OPUS_CODEC_INITIALIZE_FAILED, "opus codec initialize error !");
        codeMsg.put(OPUS_CODEC_PROCESS_FAILED, "opus codec process data error !");
        codeMsg.put(RECORDER_READ_ONE_FRAME_FAILED, "recorder process data error !");
        codeMsg.put(RECORDER_INITIALIZE_FAILED, "recorder initialize error !");


        codeMsg.put(WEBSOCKET_200, "websocket success !");
        codeMsg.put(WEBSOCKET_210, "asr init success !");
        codeMsg.put(WEBSOCKET_ASR_200, "asr识别完成结果!");
        codeMsg.put(WEBSOCKET_ASR_201, "asr识别中间结果返回！");
        codeMsg.put(WEBSOCKET_TIMEOUT, "长时间未请求业务,关闭连接");

        codeMsg.put(WEBSOCKET_SERVER_ERROR, "websocket server error !");
        codeMsg.put(WEBSOCKET_NO_NET, "websocket error cause no net !");
        codeMsg.put(WEBSOCKET_EXCEPTION, "websocket error cause exception occurred !");
        codeMsg.put(WEBSOCKET_CONNECT_ERROR, "websocket connect failed !");
        codeMsg.put(WEBSOCKET_DISCONNECT_ERROR, "websocket 连接异常断开!");

        codeMsg.put(IOT_INIT_ERRCODE, "IOT初始化错误 !");
        codeMsg.put(IOT_CONNECT_ERRCODE, "IOT MQTT连接错误 !");
        codeMsg.put(IOT_CONNECT_EXCEPTION, "IOT MQTT连接异常");
        codeMsg.put(IOT_SUBSCRIBE_EXCEPTION, "IOT MQTT订阅话题异常！");
        codeMsg.put(BIND_CODE_NULL, "Bind 获取绑定码为空");

        codeMsg.put(IOT_CONNECTION_LOST, "IOT 与服务器失去连接 !");


        codeMsg.put(CAMERA_NO_PERMISSION, "未开启相机权限");
        codeMsg.put(CAMERA_NOT_SUPPORT_CAMERA2, "当前设备不支持 Camera 2.0");
        codeMsg.put(CAMERA_CONFIG_FAIL, "相机配置失败");
        codeMsg.put(PERMISSION_NO_OFFLINE_DOC, "请检查是否授权文档检测能力！");
        codeMsg.put(DOC_NOT_FIND, "未检测到文档");
        codeMsg.put(DOC_LACK_ANGLE, "检测到文档缺角");
        codeMsg.put(DOC_LACK_EDGE, "检测到文档缺边");
        codeMsg.put(DOC_FAIL_DOWNLOAD_AUTHFILE, "鉴权文件下载失败");

        codeMsg.put(CODE_OPERATESTATE_ANSWER_SUC, "太棒了，答对了！");
        codeMsg.put(CODE_OPERATESTATE_ANSWER_ERR, "答错了，加油哦！");
        codeMsg.put(CODE_OPERATESTATE_NOIMAGE, "图片没有或者不支持");
        codeMsg.put(CODE_OPERATESTATE_INNER_ERR, "算法内部错误");
        codeMsg.put(CODE_OPERATESTATE_FAILT, "识别不到正确的算术内容");
        codeMsg.put(MATH_NO_ABILITY, "未匹配到口算批改能力");

        codeMsg.put(PERMISSION_NO_OFFLINE_POSTURE, "请检查是否授权坐姿检测能力!");
        codeMsg.put(POSTURE_INPUT_PARAMETER_ERR, "输入参数错误");
        codeMsg.put(POSTURE_REPEATED_CALL, "重复调用");
        codeMsg.put(POSTURE_USERINFO_NOT_EXIST, "用户信息不存在");
        codeMsg.put(POSTURE_NUMBER_OF_USER_REGI_USED_UP, "用户注册数量已用完");
        codeMsg.put(POSTURE_DEVICEID_EXCEEDED_REGISTERED_USERS, "获取的设备信息数量远超用户注册数量");
        codeMsg.put(POSTURE_FAILED_TO_INSERT_OR_REGISTER, "新设备插入或注册失败");
        codeMsg.put(POSTURE_NET_SERVICE_INPUT_PARAMETER_EMPTY, "网络服务输入参数为空");
        codeMsg.put(POSTURE_SOME_INPUT_PARAMETER_NULL, "某些输入参数为空");
        codeMsg.put(POSTURE_UNKNOWN_SERVER_EXCEPTION, "Unknown Server Exception(异常)");
        codeMsg.put(POSTURE_FAILED_TO_CONNECT_SERVER, "连接服务器失败");
        codeMsg.put(POSTURE_USER_DEVICEID_EXCEEDS_80BYTES, "用户设备唯一信息超过80字节");
        codeMsg.put(COMPOSITION_NOT_INIT, "作文批改未初始化");
        codeMsg.put(COMPOSITION_NOT_OFFLINE, "请检查是否授权作文批改能力!");
    }

    //    public static String getMsg(int code) {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("code", code);
//        jsonObject.addProperty("message", codeMsg.get(code));
//        return jsonObject.toString();
//    }
    public static String getMsg(int code) {
        return codeMsg.get(code);
    }

    //    public static String getMsg(int code, String message) {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("code", code);
//        jsonObject.addProperty("message", message);
//        return jsonObject.toString();
//    }
    public static String getRemoteMsg(int code, String msg) {
        return "Code:" + code + ",Message:" + msg;
    }

}
