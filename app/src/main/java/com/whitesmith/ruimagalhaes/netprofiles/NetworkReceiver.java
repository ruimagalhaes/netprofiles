package com.whitesmith.ruimagalhaes.netprofiles;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
    public NetworkReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            if (isWiFi){
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                AudioManager manager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

                SharedPreferences settings = context.getSharedPreferences(FavNetworks.PREFS_NAME, 0);
                int mode = settings.getInt(wifiInfo.getSSID(), -1);
                switch (mode) {
                    case 0:
                        Toast.makeText(context ,wifiInfo.getSSID() + "  SILENT", Toast.LENGTH_SHORT).show();
                        manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        break;
                    case 1:
                        Toast.makeText(context , wifiInfo.getSSID() + "  VIVRATE", Toast.LENGTH_SHORT).show();
                        manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        break;
                    case 2:
                        Toast.makeText(context , wifiInfo.getSSID() + "  NORMAL", Toast.LENGTH_SHORT).show();
                        manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
