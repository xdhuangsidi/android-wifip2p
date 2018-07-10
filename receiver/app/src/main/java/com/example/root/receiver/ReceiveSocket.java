package com.example.root.receiver;

import android.os.Environment;

import android.util.Log;

import com.example.root.sender.FileBean;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class ReceiveSocket implements  Runnable{

    public static final String TAG = "ReceiveSocket";
    public static final int PORT = 10000;
    private ServerSocket mServerSocket;
    private Socket mSocket;
    private InputStream mInputStream;
    private ObjectInputStream mObjectInputStream;
    private FileOutputStream mFileOutputStream;
    private File mFile;


    public void createServerSocket() {
        try {
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(PORT));
            mSocket = mServerSocket.accept();

            Log.e(TAG, "客户端IP地址 : " + mSocket.getRemoteSocketAddress());
            mInputStream = mSocket.getInputStream();
            mObjectInputStream = new ObjectInputStream(mInputStream);
            FileBean fileBean = (FileBean) mObjectInputStream.readObject();
            String name = new File(fileBean.filePath).getName();
            Log.e(TAG, "客户端传递的文件名称 : " + name);

            mFile = new File(SdCardPath(name));
            mFileOutputStream = new FileOutputStream(mFile);
            //开始接收文件

            byte bytes[] = new byte[1024];
            int len;
            long total = 0;
            int progress;
            long BeginTime = System.currentTimeMillis();
            while ((len = mInputStream.read(bytes)) != -1) {
                mFileOutputStream.write(bytes, 0, len);
                total += len;
                progress = (int) ((total * 100) / fileBean.fileLength);
                Log.e(TAG, "文件接收进度: " + progress);

            }
            long EndTime = System.currentTimeMillis();

            long SpendTime = EndTime - BeginTime;
            long speed = ( (total >> 10) * 1000 ) / (SpendTime);
            Log.e("Speed ", speed + "");

            Log.e(TAG, "文件接收成功");

            mServerSocket.close();
            mInputStream.close();
            mObjectInputStream.close();
            mFileOutputStream.close();

        } catch (Exception e) {
            Log.e(TAG, "文件接收异常");
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        createServerSocket();
    }


    /**
     * 服务断开：释放内存
     */
    public void clean() {

        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mObjectInputStream != null) {
            try {
                mObjectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String SdCardPath(String name) {
        String path = Environment.getExternalStorageDirectory() + "/wifip2p";
        File file = new File(path);
        if (file.exists()) {
            if (!file.isDirectory()) {
                file.mkdir();
            }
        } else {
            file.mkdir();
        }
        return path + "/" + name;
    }
}
