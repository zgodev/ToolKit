package com.zhangyt.utils;

//import java.io.FileInputStream;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

//import javax.microedition.io.Connector;
//import javax.microedition.io.file.FileConnection;

/**
 * 字节、字符串、16进制转换转换工具类
 */
//和外界交换任何信息都是以byte[]来进行的
public class PubUtils {
    // ///////// StringToHex:String转成byte[],再将每个byte转成两个char表示的hex码
    static File filePath = new File(Environment.getExternalStorageDirectory()
            .getPath() + File.separator + "log");

    public static String ByteArrayToHex(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret.toUpperCase();
    }

    public static String StringToHex(String strSrc) {
        byte[] tmp = strSrc.getBytes(); // 有疑问，当为不可见字符得到的都是0x3f？？
        return ByteArrayToHex(tmp);
    }
    public static byte[] byteToArray(byte b){
        byte[] bytes = new byte[1];
        bytes[0]= b;
        return bytes;
    }
    public static String TLV_BCD(String tag, String data) {// BCD
        if (data == null || data.length() == 0) {
            return "";
        } else {
            String lens = int2Hex(data.length() / 2);
            return tag + lens + data;
        }

    }

    public static String TLV_HEX(String tag, String data) {// BCD
        if (data == null || data.length() == 0) {
            return "";
        } else {
            String lens = int2Hex(data.length() / 2);
            return tag + lens + data;
        }

    }

    public static String TLV_ASC(String tag, String data) {// BCD
        if (data == null || data.length() == 0) {
            return "";
        } else {
            String lens = int2Hex(data.length());
            String dates = PubUtils.StringToHex(data);
            return tag + lens + dates;
        }

    }

    public static String TLV_CN(String tag, String data) {// BCD
        if (data == null || data.length() == 0) {
            return "";
        } else {
            String lens = long2Hex(data.length() / 2);
//			String datas = PubUtils.StringToHex(data);
            return tag + "82" + lens + data;
        }

    }

    // StringToHexString没有补位0
    public static String StringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }
        return hexString;
    }

    // //////// HexToString:先将每两个ASCII字符合成一个字节然后转成byte，再将每个byte转成对应的字符串
    // 注意到byte的取值范围为-127~128
    public static String HexToString(String s) {
        if (s.length() % 2 != 0)// 如果长度为奇数返回为空，说明输入有错
            return null;

        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                // baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                // i * 2, i * 2 + 2), 16));
                int n = 0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2),
                        16);
                baKeyword[i] = (byte) n;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 把byte转化成string，必须经过编码
        try {
             s = new String(baKeyword, "utf-8");// UTF-16le:Not
//            s = new String(baKeyword);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return s;
    }

    /**
     * 两位变一位
     *
     * @param s
     * @return
     */
    public static String HexToStringBuf(String s) {
        if (s.length() % 2 != 0)// 如果长度为奇数返回为空，说明输入有错
            return null;
        StringBuilder sb = new StringBuilder();
        int[] baKeyword = new int[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                // baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                // i * 2, i * 2 + 2), 16));
                int n = Integer.parseInt(s.substring(i * 2, i * 2 + 2));
                baKeyword[i] = n;
                sb.append(baKeyword[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String s1 = sb.toString();
        return s1;
    }

    // //////// StrToHex,将形如“3a5867”转化成“0x3A，0x58，0x67”
    // 注意传入str之前需先将小写转成大写
    public static byte[] StrToHex(String str) {
        if (str.length() % 2 != 0) {
            // return null;
            str = str + "F";
        }
        byte[] ret = new byte[str.length() / 2];
        Arrays.fill(ret, (byte) 0xFF);

        if (IsIlleagalStr(str))
            return ret;
        StringBuffer buf = new StringBuffer(2);
        int j = 0;
        for (int i = 0; i < str.length(); i++, j++) {
            buf.insert(0, str.charAt(i));
            buf.insert(1, str.charAt(i + 1));
            int t = Integer.parseInt(buf.toString(), 16);

            ret[j] = (byte) t;
            i++;
            buf.delete(0, 2);
        }
        return ret;
    }

    // ///////// HexTostr,将“0x3A，0x58，0x67”变成“3A5876”
    public static String HexTostr(byte[] hex) {
        StringBuffer buff= new StringBuffer();

        if (hex==null)
            return "";
        if (hex.length == 0)
            return "";
        for (int i = 0; i < hex.length; i++) {
            String buf = Integer.toString(hex[i] & 0xFF, 16);// & 0xFF去掉负号
            if (buf.length() != 2)// 补位
            {
                buff.append("0" + buf);
            } else
                buff.append(buf);
        }
        return buff.toString().toUpperCase();
    }

    public static String HexTostr(int hex) {
        String str = "";

        String buf = Integer.toString(hex & 0xFF, 16);// & 0xFF去掉负号
        if (buf.length() != 2)// 补位
        {
            str += "0" + buf;
        } else
            str += buf;

        return str;
    }

    public static String HexTostr(byte[] hex, int length) {
        String str = "";
        if ((hex.length == 0) || (length == 0))
            return str;
        for (int i = 0; i < length; i++) {
            String buf = Integer.toString(hex[i] & 0xFF, 16);// & 0xFF去掉负号
            if (buf.length() != 2)// 补位
            {
                str += "0" + buf;
            } else
                str += buf;
        }
        return str;
    }

    public static byte[] AscciToHex(byte[] ascci, int length) {

        if ((ascci.length == 0) || (length == 0))
            return null;
        int len = Math.min(ascci.length, length);
        byte[] buf = new byte[len];
        System.arraycopy(ascci, 0, buf, 0, len);
        String strTmp;
        try {
            strTmp = new String(buf, 0, len, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return StrToHex(strTmp);
    }

    public static byte[] HexToAscci(byte[] hex, int length) {

        if ((hex.length == 0) || (length == 0))
            return null;
        int len = Math.min(hex.length, length);
        byte[] buf = new byte[len];
        System.arraycopy(hex, 0, buf, 0, len);

        String strTmp = HexTostr(buf);
        return strTmp.getBytes();
    }

    /**
     * @param s
     * @return
     */
    public static byte[] str2byteArray(String s) {
        byte[] buf = new byte[s.length()];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) s.charAt(i);
        }
        return buf;
    }

    /**
     * 十六进制String 转成 ASCII码字符串
     *
     * @param s
     * @return
     */
    public static String HexToAscci(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "ASCII");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    // //////////////将16进制数转换为字符串并按要求补位，如0x123输出占四个字节的为“0123”
    public static String PaddingHexintToString(int src, int totalsize) {
        String target;
        target = Integer.toString(src, 16);
        if (target.length() > totalsize)
            return null;
        while (target.length() < totalsize) {
            target = "0" + target;
        }
        return target;
    }

    /**
     * 大小端互换
     * "0000014d"--->"4d010000"
     *
     * @param src
     * @param totalsize
     * @return
     */
    public static String int2Str(int src, int totalsize) {
        String target, result;
        target = Integer.toString(src, 16);
        if (target.length() > totalsize)
            return null;
        for (int i = target.length(); i < totalsize; i++) {
            target = "0" + target;
        }
        int length = target.length() / 2;
        String[] buf1 = new String[length];
        String[] buf2 = new String[length];
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < length; i++) {
            buf1[i] = target.substring(count, count + 2);
            count += 2;
            buf2[length - i - 1] = buf1[i];
        }
        for (int i = 0; i < buf2.length; i++) {
            sb.append(buf2[i]);
        }
        return sb.toString();
    }

    // ////////byte数组与int类型的转换，在socket传输中，发送、者接收的数据都是byte数组，但是int类型是4个byte组成的，如何把一个整形int转换成byte数组，同时如何把一个长度为4的byte数组转换为int类型,需要两个简单的算法：
    // 与VC中高低位顺序恰好相反2008.11.26
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    // 十进制 转 十六进制 判断是否加0
    public static String int2Hex(int res) {
        String shift;
        if (res < 16) {
            shift = "0" + Integer.toHexString(res);
        } else if (res > 255 && res < 4096) {
            shift = "0" + Integer.toHexString(res);
        } else {
            shift = Integer.toHexString(res);
        }
        return shift;
    }

    // 十进制 转 十六进制 判断是否加0
    public static String long2Hex(int res) {
        String shift;
        if (res < 16) {
            shift = "00" + "0" + Integer.toHexString(res);

        } else if (res > 255 && res < 4096) {
            shift = "0" + Integer.toHexString(res);
        } else {
            shift = "00" + Integer.toHexString(res);
        }
        return shift;
    }

    // BCD 转String
    public static String bcd2String(String res) {
        StringBuilder sb = new StringBuilder();
        char[] str = res.toCharArray();
        for (int i = 0; (2 * i + 1) < str.length; i++) {
            sb.append(str[2 * i + 1]);
        }
        return sb.toString();
    }

    public static int byte2int(byte[] res) {
        int targets = 0;
        if (res.length <= 4) {
            if (res.length == 1)
                targets = (int) (res[0] & 0xFF);
            else if (res.length == 2)
                //targets = (res[0] & 0xff) | (res[1] << 8);
                targets = (res[0] << 8) | (res[1] & 0xFF);
            else if (res.length == 3)
                //targets = (res[0] & 0xff) | (res[1] << 8) | (res[2] << 16);
                targets = (res[0] << 16) | (res[1] << 8) | (res[2] & 0xFF);
            else
                targets = (res[0] << 24) | (res[1] << 16)
                        | (res[2] << 8) | (res[3] & 0xFF);
        }
        return targets;
    }

    public static void Memcpy(byte[] output, byte[] input, int outpos,
                              int inpos, int len) {
        int i;
        for (i = 0; i < len; i++)
            output[outpos + i] = input[inpos + i];
    }

    // 路径格式： path = "file:///e:/log/log.txt";
    public static String FcopVersion() {
        return System
                .getProperty("microedition.io.file.FileConnection.version");
    }

    // 读文件
    public static byte[] showFile(String fileName) {

        /*
         * FileConnection fc = null; byte buf[] = null; try { fc =
         * (FileConnection) Connector.open(fileName); if (!fc.exists())// 文件是否存在
         * throw new IOException("file no exists"); // fc.setReadable(true);
         * InputStream is = fc.openInputStream(); int len = (int)
         * fc.availableSize(); if (len == 0) len = 1024; byte bOut[] = new
         * byte[len]; int length = is.read(bOut, 0, len); buf = new
         * byte[length]; Memcpy(buf, bOut, 0, 0, length); is.close(); } catch
         * (IOException ex) { System.out.println(ex.toString()); }
         */
        File file = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new java.io.FileReader(file));
            StringBuffer sb = new StringBuffer();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            br.close();
            return sb.toString().getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    // 修改文件
    public static void modifyFile(String path, byte[] newData) {
        byte[] buf = showFile(path);
        int len = buf.length + newData.length;
        byte Data[] = new byte[len];
        Memcpy(Data, buf, 0, 0, buf.length);
        Memcpy(Data, newData, buf.length, 0, newData.length);
        // 先删再写
        DeleteFile(path);
        WriteFile(path, Data);
    }

    // 删除文件
    public static void DeleteFile(String path) {

        /*
         * try { FileConnection fc = (FileConnection) (Connector.open(path)); if
         * (!fc.exists()) throw new IOException("file exists"); fc.delete(); }
         * catch (Exception e) { System.out.println("saveFileErr:" +
         * e.toString()); }
         */
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("File Not Exist");
            return;
        }
        if (file.delete()) {

        } else {
            System.out.println("File Delete Fail");

        }

    }

    // 创建文件,如果文件存在直接删除不保留原文件数据
    public static void WriteFile(String path, byte[] fileData) {
        File file = new File(path);
        if (file.exists()) {
            DeleteFile(path);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(fileData);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    // public static void Writelog(String str) {
    // String path = "file:///e:/log/log.txt";
    // byte[] buf = str.getBytes();
    // WriteFile(path, buf);
    // }
    public static void Writelog(String str) {

        String a = str;
        OutputStream os = null;
        // SD card exist or not
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (!filePath.exists()) {
                filePath.mkdir();// 如果路径不存在就先创建路径
            }
            File file = new File(filePath, p);

            try {
                os = new FileOutputStream(file, true);
                os.write("\n".getBytes());
                // os.write(fileName.getBytes());
                os.write("\n".getBytes());
                os.write(a.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    public static void Writelog(byte[] data) {

        OutputStream os = null;
        // SD card exist or not
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (!filePath.exists()) {
                filePath.mkdir();
            }
            File file = new File(filePath, p);

            try {
                os = new FileOutputStream(file, true);
                os.write("\n".getBytes());
                // os.write(fileName.getBytes());
                os.write("\n".getBytes());
                os.write(data);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    public static void Modifylog(String str) {
        String path = "file:///e:/log/log.txt";
        byte[] buf = str.getBytes();
        modifyFile(path, buf);
    }

    // public static byte[] reverse(byte[]bInData){
    // byte[]buf=new byte[bInData.length];
    // for(int i=0;i<bInData.length;i++)
    // {
    // buf[i]=bInData[bInData.length-i];
    // }
    // return buf;
    // }
    public static void reverse(byte[] pbData) {
        byte temp;
        int l, b, c;
        l = pbData.length / 2;
        for (b = 0, c = pbData.length - 1; b < l; b++, c--) {
            temp = pbData[b];
            pbData[b] = pbData[c];
            pbData[c] = temp;
        }
    }

    // 得到APDU指令返回的数据data即RetDataAPDU中的变量retData；nOff即取偏移；nDatalen即从偏移位置起要使用的数据的长度
    public static String GetApduResponse(byte[] bData, int nOff, int nDatalen) {
        String strRetData = "";
        if (bData == null)
            return strRetData;
        if ((nOff < 0) || (nDatalen < 0) || (nOff + nDatalen > bData.length))
            return strRetData;

        int nRet = 0;
        for (int i = 0; i < nDatalen; i++) {
            if (bData[nOff + i] < 0)// 由于byte取值范围是-127~128
                nRet = 256 + bData[nOff + i];
            else
                nRet = bData[nOff + i];

            if (nRet < 0x10)// 补位，占两位
                strRetData += ("0");
            strRetData += (Integer.toHexString(nRet) + " ");
            if (i % 8 == 7)// 分行显示，每行显示8个字符
                strRetData = strRetData.concat("\n");
        }
        return strRetData;
    }

    // 得到设备的状态码,即返回数据的最后四位,占两个字节
    public static int GetDeviceStateResponse(byte[] retData, int nretDatalen) {
        byte[] b = new byte[2];
        PubUtils.Memcpy(b, retData, 0, nretDatalen - 2, 2);
        String buf = PubUtils.ByteArrayToHex(b);
        if (nretDatalen == 0x80)
            PubUtils.WriteLogHex("b", b);
        int n = Integer.parseInt(buf, 16);
        return n;
    }

    // 得到设备的返回值
    public static String GetApudReturnValue(byte[] retData, int nOff,
                                            int nretDatalen) {
        byte[] b = new byte[nretDatalen - 2];
        PubUtils.Memcpy(b, retData, 0, nOff, nretDatalen - 2);
        String buf = PubUtils.ByteArrayToHex(b);
        return buf;
    }

    // 去掉字符串中的空格
    public static String TrimSpace(String strMsg) {
        String buf = strMsg.trim();// 去掉字符串首末空格
        String ret = "";
        for (int i = 0; i < buf.length(); i++) {
            if (buf.charAt(i) != ' ')
                ret += buf.charAt(i);
        }
        return ret;
    }

    /*
     * 函数说明：saveFile保存日志信息
     */
    public static void saveFile(String path, byte[] fileData)// 保存文件
    {

        OutputStream os = null;
        File file = new File(path);
        if (file.exists()) {
            int size = (int) file.length();
            size = size + 20;

        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os = new FileOutputStream(file);
            os.write(fileData);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         *
         * try { FileConnection fc = (FileConnection) (Connector.open(path,
         * Connector.READ | Connector.WRITE)); if (fc.exists()) { int size =
         * (int) fc.fileSize(); size = size + 20; os =
         * fc.openOutputStream(size); os.write(fileData); os.close(); }
         * fc.create(); os = fc.openOutputStream(); os.write(fileData);
         * os.close(); } catch (Exception e) {
         *
         * }
         */

    }

    public static String p = "android.txt";

    public static void WriteLog(String strTag, byte[] fileData)// 保存文件
    {
        OutputStream os = null;
        // SD card exist or not
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (!filePath.exists()) {
                filePath.mkdir();// 如果路径不存在就先创建路径
            }
            File file = new File(filePath, p);

            String a = ByteArrayToHex(fileData);
            try {
                os = new FileOutputStream(file, true);
                os.write("\n".getBytes());
                os.write(strTag.getBytes());
                os.write(" ".getBytes());
                os.write(fileData);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void WriteLog(String strTag, String fileData)// 锟斤拷锟斤拷锟侥硷拷
    {
        OutputStream os = null;
        // SD card exist or not
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (!filePath.exists()) {
                filePath.mkdir();
            }
            File file = new File(filePath, p);

            try {
                os = new FileOutputStream(file, true);
                os.write("\n".getBytes());
                os.write(strTag.getBytes());
                os.write(" ".getBytes());
                os.write(fileData.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String BinToHex(String Src, int SrcLen) {
        int i, result = 0, k = 1, j = 0;
        String Des = "";
        SrcLen = SrcLen - SrcLen % 4;
        for (i = SrcLen - 1; i >= 0; i--) {
            if (Src.charAt(i) == '1')
                result += 1 << (k - 1); // 如果是1，用1*位权
            if (k == 4 || i == 0) // 每四位计算一次结果（result）。
            // 如果到了最高位（i==0）不足四位（比如100 0000），也计算
            {
                switch (result) {
                    case 10:
                        Des = Des + "A";
                        break; // 大于等于十转化成字母
                    case 11:
                        Des = Des + "B";
                        break;
                    case 12:
                        Des = Des + "C";
                        break;
                    case 13:
                        Des = Des + "D";
                        break;
                    case 14:
                        Des = Des + "E";
                        break;
                    case 15:
                        Des = Des + "F";
                        break;
                    default:
                        Des = Des + (char) (result + 0x30);
                        break;
                }
                result = 0; // 结果清零
                k = 0; // 表示位权的K清零
            }
            k++; // 初始位权为1
        }
        // Des = Des+"\0";
        return Des;
    }

    public static void WriteLogHex(String fileName, byte[] fileData)// 保存文件
    {
        /*
         * String a = ByteArrayToHex(fileData);
         *
         * OutputStream os; try { FileConnection fc = (FileConnection)
         * (Connector.open(p, Connector.READ | Connector.WRITE)); if
         * (fc.exists()) { int size = (int) fc.fileSize(); size = size + 20; os
         * = fc.openOutputStream(size); os.write("\n".getBytes());
         * os.write(fileName.getBytes()); os.write("\n".getBytes());
         * os.write(a.getBytes()); os.close(); } fc.create(); os =
         * fc.openOutputStream(); os.write("\n".getBytes());
         * os.write(fileName.getBytes()); os.write("\n".getBytes());
         * os.write(a.getBytes()); os.close(); } catch (Exception e) {
         *
         * }
         *
         * }
         */
        String a = ByteArrayToHex(fileData);
        OutputStream os = null;
        // SD card exist or not
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (!filePath.exists()) {
                filePath.mkdir();// 如果路径不存在就先创建路径
            }
            File file = new File(filePath, p);

            try {
                os = new FileOutputStream(file, true);
                os.write("\n".getBytes());
                os.write(fileName.getBytes());
                os.write("\n".getBytes());
                os.write(a.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    public static String tlvEncode(String tag, String value) {
        String sums = tag + HexTostr(Integer.toHexString(value.length() / 2))
                + value;
        return sums;
    }

    public static String HexTostr(String buf) {
        String sb = "";
        if (buf.length() != 2) {
            sb = "0" + buf;
        } else {
            sb = buf;
        }
        return sb;
    }

    public static String tlvDecode(String msg, String tag) {
        String s = msg.substring(msg.indexOf(tag) + tag.length(),
                msg.indexOf(tag) + tag.length() + 2);
        int i = Integer.parseInt(s, 16);
        String zh = msg.substring(msg.indexOf(tag) + tag.length() + 2,
                msg.indexOf(tag) + tag.length() + 2 + i * 2);
        return zh;
    }

    public static byte[] createRandom(int len) {
        byte[] ret = new byte[len];
        /*
         * if(len ==0) return null;
         *
         * for(int i=0; i< len;i++){ Random random= new
         * Random(System.currentTimeMillis()+i); ret[i] =
         * (byte)random.nextInt(255); }
         */
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(ret);
        return ret;

    }

    public static byte[] short2byteBigendian(int res) {
        byte[] targets = new byte[2];
        targets[1] = (byte) (res & 0xff);
        targets[0] = (byte) ((res >> 8) & 0xff);
        return targets;
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static String utf82gbk(String utf) {
        String l_temp = utf8ToUnicode(utf);
        l_temp = Unicode2GBK(l_temp);

        return l_temp;
    }

    /**
     * utf-8 转unicode
     *
     * @param inStr
     * @return String
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * @param dataStr
     * @return String
     */

    public static String Unicode2GBK(String dataStr) {
        int index = 0;
        StringBuffer buffer = new StringBuffer();

        int li_len = dataStr.length();
        while (index < li_len) {
            if (index >= li_len - 1
                    || !"\\u".equals(dataStr.substring(index, index + 2))) {
                buffer.append(dataStr.charAt(index));

                index++;
                continue;
            }

            String charStr = "";
            charStr = dataStr.substring(index + 2, index + 6);

            char letter = (char) Integer.parseInt(charStr, 16);

            buffer.append(letter);
            index += 6;
        }

        return buffer.toString();
    }

    public static String FormatCash(String sTransAmount, String format)// //14.25转成000000001425
    {
        String sAmount = "";
        int i = sTransAmount.indexOf(".");
        if (i == -1) {
            sAmount = sTransAmount + "00";
        } else {
            sAmount = sTransAmount.substring(0, i)
                    + sTransAmount.substring(i + 1, sTransAmount.length());
            if (sTransAmount.substring(i + 1, sTransAmount.length()).length() == 1) {
                sAmount = sAmount + "0";
            }
        }
        String fAmount = String.format(format, Integer.parseInt(sAmount));
        return fAmount;
    }

    public static boolean IsIlleagalStr(String s) {
        int i = 0;
        for (; i < s.length(); i++) {
            if (!((s.charAt(i) >= '0' && s.charAt(i) <= '9')
                    || (s.charAt(i) >= 'a' && s.charAt(i) <= 'f') || (s
                    .charAt(i) >= 'A' && s.charAt(i) <= 'F'))) {
                return true;
            }
        }
        return false;
    }

    public void intToByte(byte[] Des, int Src) {
        Des[0] = (byte) (Src & 0xFF);
        Des[1] = (byte) (Src >> 8);
    }

    public static String toAllUpperOrLowerCase(String fireString) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < fireString.length(); i++) {
            char c = fireString.charAt(i);
            if (Character.isUpperCase(c)) {
                buffer.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                buffer.append(Character.toUpperCase(c));
            }
        }
        return buffer.toString();
    }

    public static String FormatTxtCash(String sTransAmount)// //14.25转化为1425////14.20转化为1420
    {
        String sAmount = sTransAmount.substring(0, sTransAmount.indexOf("."))
                + sTransAmount.substring(sTransAmount.indexOf(".") + 1,
                sTransAmount.length());
        if (sTransAmount.substring(sTransAmount.indexOf(".") + 1,
                sTransAmount.length()).length() == 1) {
            sAmount = sAmount + "0";
        }
        String fAmount = String.format("%d", Integer.parseInt(sAmount));
        return fAmount;
    }

    public static String FormatCash(String sTransAmount)// //14.25转化为000000001425
    {
        if (sTransAmount == null) {
            return "";
        }
        String sAmount = "";
        int i = sTransAmount.indexOf(".");
        if (i == -1) {
            sAmount = sTransAmount + "00";
        } else {
            sAmount = sTransAmount.substring(0, i)
                    + sTransAmount.substring(i + 1, sTransAmount.length());
            if (sTransAmount.substring(i + 1, sTransAmount.length()).length() == 1) {
                sAmount = sAmount + "0";
            }
        }
        String fAmount = String.format("%012d", Integer.parseInt(sAmount));

        return fAmount;
    }

    public static float toCash(String formatCash)// /// 00001245转化为 12.45
    {
        if (formatCash.length() != 12)
            return 0.00f;
        if (formatCash.equals("000000000000"))
            return 0.00f;

        String fcash = formatCash.substring(0, formatCash.length() - 2) + "."
                + formatCash.substring(formatCash.length() - 2, formatCash.length());
        return Float.parseFloat(fcash);

    }

    public static int HexToInt(String s) {
        int len = 0;
        byte[] temp = StrToHex(s);
        len = byte2int(temp);
        return len;
    }


    public static HashMap<String, String> TLV_HashMap_Value(String s, int offs) {
        HashMap<String, String> resultMap = new HashMap<String, String>();
        int len = 0;
        int off = 0;
        String res;
        String tag;

        while (offs <= s.length() - 4) {

            tag = TLV_itag(s, offs);
            int length = Integer.parseInt("102", 16);
            off = offs + tag.length();
            String tem = s.substring(off, off + 2);
            len = PubUtils.HexToInt(tem);
            off += 2;
            if (len > 0x80) {
                if (len == 0x81) {
                    len = PubUtils.HexToInt(s.substring(off, off + 2));
                    off += 2;
                } else if (len == 0x82) {
                    String tem2 = s.substring(off, off + 4);
//					len=Integer.parseInt(tem2, 16);
                    len = PubUtils.HexToInt(tem2);
                    off += 4;
                }
            }

            res = s.substring(off, off + len * 2);
            offs = off + len * 2;
            resultMap.put(tag, res);
            // resultMap.put(tag, HexToAscci(res)); //转成ASCII后的数据
        }

        return resultMap;
    }

    public static LinkedHashMap<String, String> TLV_HashMap_Value1(String s, int offs) {
        LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();
        int len = 0;
        int off = 0;
        String res;
        String tag;

        while (offs <= s.length() - 4) {

            tag = TLV_itag(s, offs);
            int length = Integer.parseInt("102", 16);
            off = offs + tag.length();
            String tem = s.substring(off, off + 2);
            len = PubUtils.HexToInt(tem);
            off += 2;
            if (len > 0x80) {
                if (len == 0x81) {
                    len = PubUtils.HexToInt(s.substring(off, off + 2));
                    off += 2;
                } else if (len == 0x82) {
                    String tem2 = s.substring(off, off + 4);
//					len=Integer.parseInt(tem2, 16);
                    len = PubUtils.HexToInt(tem2);
                    off += 4;
                }
            }

            res = s.substring(off, off + len * 2);
            offs = off + len * 2;
            resultMap.put(tag, res);
            // resultMap.put(tag, HexToAscci(res)); //转成ASCII后的数据
        }

        return resultMap;
    }

    public static HashMap<String, String> TlvProcess(String[] resultData) {
        // TODO Auto-generated method stub
        HashMap<String, String> resultMap = new HashMap<String, String>();
        HashMap<String, String> resultMap1 = new HashMap<String, String>();

        // System.out.print("\n resultData.length:"+resultData.length);

        for (int i = 0; i < resultData.length; i++) {
            if ((resultData[i] == null) || (resultData[i].length() == 0))
                break;
            // System.out.print("\n resultData[i]=:"+resultData[i]);
            // System.out.print("\n resultData i=:"+i);

            resultMap1 = TLV_HashMap_Value(resultData[i], 0);
            resultMap.putAll(resultMap1);
        }

        return resultMap;
    }

    public static byte[] getByteArray(String hexString) {
        byte[] hexbyte = hexString.getBytes();
        byte[] bitmap = new byte[hexbyte.length / 2];
        for (int i = 0; i < bitmap.length; i++) {
            hexbyte[i * 2] -= hexbyte[i * 2] > '9' ? 7 : 0;
            hexbyte[i * 2 + 1] -= hexbyte[i * 2 + 1] > '9' ? 7 : 0;
            bitmap[i] = (byte) ((hexbyte[i * 2] << 4 & 0xf0) | (hexbyte[i * 2 + 1] & 0x0f));
        }
        return bitmap;
    }

    /**
     * 乐源手环指令封装
     *
     * @param startFlag
     * @param endflag
     * @param commandCode
     * @param action
     * @param data
     * @return
     */
    public static byte[] formatWristData(byte startFlag, byte endflag, byte commandCode, byte action, byte[] data) {

        int totalLen = 6 + data.length;
        byte[] dataSend = new byte[totalLen];
        dataSend[0] = startFlag;
        dataSend[1] = commandCode;
        dataSend[2] = action;
        dataSend[(totalLen - 1)] = endflag;
        System.arraycopy(PubUtils.int2byte(data.length), 0, dataSend, 3, 2);
        System.arraycopy(data, 0, dataSend, 5, data.length);
        return dataSend;
    }

    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
            System.out.format("%02X\n", bbt[p]);
        }
        return bbt;
    }

    private static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }

    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
            System.out.format("%02X\n", bcd[i]);
        }
        return bcd;
    }

    public static String bcd2Str(byte[] bytes) {
        char temp[] = new char[bytes.length * 2], val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }


    public static int TLV_NextOffset(String src, int offs) {
        int ltag, tmpl;
        String tmptag = TLV_itag(src, offs);
        ltag = tmptag.length();
        offs += ltag;
        tmpl = PubUtils.HexToInt(src.substring(offs, offs + 2));
        if (tmpl > 0x80) {
            if (tmpl == 0x81) {
                tmpl = PubUtils.HexToInt(src.substring(offs + 2, offs + 4));
                offs += 4;
            }
        } else {
            offs += 2;
        }
        offs += (tmpl * 2);
        return offs;
    }

    public static String TLV_Data(String s, int offs) {
        int i = TLV_NextOffset(s, offs);
        String res = s.substring(offs, i);
        return res;
    }


    public static int FindTLVByTag(String src, String tag, String TemplateTag) {
        int res = -1;
        int offs = 0;
        String tmptag = "";
        if (TemplateTag.length() != 0) { //if Template exist
            offs = TLV_NextOffset(src, 0);
            System.out.print("=== offs =" + offs);
            if (offs != src.length())
                return -1;
            tmptag = TLV_itag(src, 0);
            //if (TemplateTag != tmptag)

            offs = tmptag.length();
            offs += ((TLV_ilen(src, 0) > 0x80) ? 4 : 2);
            System.out.print("\n===111 offs =" + offs);
        }
        while (offs < src.length()) {//--while
            tmptag = TLV_itag(src, offs);
            System.out.print("===tmptag =" + tmptag);
            if (tag.equalsIgnoreCase(tmptag)) {
                res = offs;
                break;
            }
            offs = TLV_NextOffset(src, offs);
        }//--while
        return res;
    }


    public String GetValueByTag(HashMap<String, String> resultMap, String tag) {
        String res = resultMap.get(tag);
        return res;
    }

    public static String TLV_itag(String s, int offs) {
        String stag = "";
        int tag = 0;
        stag = s.substring(offs, offs + 2);
        byte[] tempData = PubUtils.StrToHex(stag);  //   HexToInt(stag);
        tag = PubUtils.byte2int(tempData);
        if ((tag & 0x1f) == 0x1f) {
            stag = s.substring(offs, offs + 4);
        }
        return stag;
    }
		        
	/*	 function TLV_stag(s,offs)
		 {
		 	var tag = TLV_itag(s,offs); 	 	
		 	if (tag>0xff) 	{	 return IntToHex(tag,4)  }else  { 	return IntToHex(tag,2);  }
		 } */

    public static int TLV_ilen(String s, int offs) {
        String tag = TLV_itag(s, offs);
        int off = 0;
        int len = 0;
        off = offs + tag.length();
        len = PubUtils.HexToInt(s.substring(off, off + 2));
        if (len > 0x80) {
            if (len == 0x81) {
                len = PubUtils.HexToInt(s.substring(off + 2, off + 4));
            } else if (len == 0x82) {
                len = PubUtils.HexToInt(s.substring(off + 2, off + 6));
            }
        }
        return len;
    }

    public static String TLV_Value(String s, int offs) {
        String tag = TLV_itag(s, offs);
        int len = 0;
        int off;
        String res;

        off = offs + tag.length();

        len = PubUtils.HexToInt(s.substring(off, off + 2));
        off += 2;
        if (len > 0x80) {
            if (len == 0x81) {
                len = PubUtils.HexToInt(s.substring(off, off + 2));
                off += 2;
            } else if (len == 0x82) {
                len = PubUtils.HexToInt(s.substring(off, off + 4));
                off += 4;
            }
        }

        res = s.substring(off, off + len * 2);
        return res;
    }

    public static String MoneyFormat(String Money, int radix) {
        String myMoney = null;

        int tmp = Integer.parseInt(Money, radix);
        Log.d("PUB UTILS", "long money = " + tmp);

        String s = Integer.toString(tmp);
        if (s.equals("0")) {
            myMoney = "0.00";
            return myMoney;
        }
        String tmpInteger = s.substring(0, s.length() - 2);
        String tmpDecimal = s.substring(s.length() - 2);
        myMoney = tmpInteger + "." + tmpDecimal;
        return myMoney;
    }

    public static String IntMoneyFormat(int Money) {
        String myMoney = null;

        String s = Integer.toString(Money);
        if (s.equals("0")) {
            myMoney = "0.00";
            return myMoney;
        }
        String tmpInteger = s.substring(0, s.length() - 2);
        String tmpDecimal = s.substring(s.length() - 2);
        myMoney = tmpInteger + "." + tmpDecimal;
        return myMoney;
    }

    /****
     * 删除F填充
     * @param obj
     * @return
     */
    public static String deletePADF(String obj) {
        int index = -1;
        if (obj != null && !obj.equals("")) {
            index = obj.indexOf("F");
            if (index != -1) {
                return obj.substring(0, index);
            } else {
                return obj;
            }
        } else {
            return "";
        }
    }

    /**
     * 从二磁数据中解析出账号
     *
     * @param obj
     * @return
     */
    public static String getAccountFromTwoTrack(String obj) {
        int index = -1;
        if (obj != null && !obj.equals("")) {
            index = obj.indexOf("D");
            if (index != -1) {
                return obj.substring(0, index);
            } else {
                return obj;
            }
        } else {
            return "";
        }
    }

    public static String StringXor(String str1, String str2) {
        BigInteger big1 = new BigInteger(str1, 16);
        BigInteger big2 = new BigInteger(str2, 16);
        return big1.xor(big2).toString(16);
    }

    /**
     * sha1,sha256 算法
     *
     * @param method sha-1, sha-256
     * @param datas
     * @return
     */
    public static String SHA_Encrypt(String method, byte[] datas) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != md) {
            md.update(datas);
            byte[] digestRes = md.digest();
            String digestStr = getDigestStr(digestRes);
            return digestStr;
        }

        return null;
    }

    private static String getDigestStr(byte[] origBytes) {
        String tempStr = null;
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < origBytes.length; i++) {
            // System.out.println("and by bit: " + (origBytes[i] & 0xff));
            // System.out.println("no and: " + origBytes[i]);
            // System.out.println("---------------------------------------------");
            // 这里按位与是为了把字节转整时候取其正确的整数，java中一个int是4个字节
            // 如果origBytes[i]最高位为1，则转为int时，int的前三个字节都被1填充了
            tempStr = Integer.toHexString(origBytes[i] & 0xff);
            if (tempStr.length() == 1) {
                stb.append("0");
            }
            stb.append(tempStr);

        }
        return stb.toString();
    }
    /**
     * 获取16进制随机数
     *
     * @param len
     * @return
     */
    public static String randomHexString(int len) {
        try {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < len; i++) {
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString().toUpperCase();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
        return null;
    }
}
