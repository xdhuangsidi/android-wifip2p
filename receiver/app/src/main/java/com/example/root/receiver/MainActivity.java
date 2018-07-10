package com.example.root.receiver;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements  Wifip2pActionListener {

    Button bt_start_watch, bt_stop_watch;

    public WifiP2pManager mWifiP2pManager;
    public WifiP2pManager.Channel mChannel;
    public Wifip2pReceiver mWifip2pReceiver;


    ReceiveSocket socketserver = new ReceiveSocket();
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_start_watch = findViewById(R.id.bt_start);
        bt_stop_watch = findViewById(R.id.bt_stop);
        initWifiP2P();

        bt_start_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });

        bt_stop_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeGroup();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(mWifip2pReceiver);
        mWifiP2pManager.removeGroup(mChannel, null);
        mWifiP2pManager.cancelConnect(mChannel, null);
        socketserver.clean();
        mWifip2pReceiver = null;
    }



    private void initWifiP2P() {


        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), this);

        //注册广播
        mWifip2pReceiver = new Wifip2pReceiver(mWifiP2pManager, mChannel, this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(mWifip2pReceiver, intentFilter);
    }


    /**
     * 创建组群，等待连接
     */
    public void createGroup() {

        mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Toast.makeText(MainActivity.this, "监听端口成功", Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "监听端口失败，端口已监听或wifi不可用， 错误序号：" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 移除组群
     */
    public void removeGroup() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "取消监听成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "取消监听失败，端口没有启动，错误序号" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }













    @Override
    public void wifiP2pEnabled(boolean enabled) {
        if (enabled) {
            thread = new Thread(socketserver);
            thread.start();
            Log.e(Wifip2pReceiver.TAG, "wipi p2p enable");
        } else {
            Toast.makeText(this, "wifi p2p不可用", Toast.LENGTH_SHORT).show();
            bt_start_watch.setClickable(false);
        }


    }

    @Override
    public void onConnection(WifiP2pInfo wifiP2pInfo) {
        Log.e(Wifip2pReceiver.TAG, "on Connection !\n" + wifiP2pInfo.toString());



    }

    @Override
    public void onDisconnection() {
        Log.e(Wifip2pReceiver.TAG, "on Disconnection !\n" );
    }

    @Override
    public void onDeviceInfo(WifiP2pDevice wifiP2pDevice) {
        Log.e("p2p device info",wifiP2pDevice.toString());

    }

    @Override
    public void onPeersInfo(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        Log.e(Wifip2pReceiver.TAG, "on PeersInfo !\n" );
        for (WifiP2pDevice s : wifiP2pDeviceList) {
            Log.e(Wifip2pReceiver.TAG, "on PeersInfo ---" + s.toString() );
        }

    }

    @Override
    public void onChannelDisconnected() {
        Log.e(Wifip2pReceiver.TAG, "channel disconnected !\n" );
    }
}
