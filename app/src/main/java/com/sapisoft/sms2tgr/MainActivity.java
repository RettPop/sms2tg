package com.sapisoft.sms2tgr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity
{
    private static final int MY_PERMISSION_SEND_MSG_REQUEST_CODE = 88;
    private static final String LOG_TAG = "SMS2TG.MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initSSL();
        setContentView(com.sapisoft.sms2tgr.R.layout.activity_main);

        String botId = getSharedPreferences("data", Context.MODE_PRIVATE).getString("botId", "");
        String chatId = getSharedPreferences("data", Context.MODE_PRIVATE).getString("chatId", "");
        EditText txtBotId = findViewById(R.id.botId);
        txtBotId.setText(botId, TextView.BufferType.EDITABLE);
        EditText txtChatId = findViewById(R.id.chatId);
        txtChatId.setText(chatId, TextView.BufferType.EDITABLE);

        setupPermissions();
    }

    private void startFwdService()
    {
        Log.i(LOG_TAG, "Starting service");
        Intent startServiceIntent = new Intent(this, ForwardSMSService.class);
        startServiceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startServiceIntent);
    }

    private void stopFwdService()
    {
        Intent startServiceIntent = new Intent(this, ForwardSMSService.class);
        stopService(startServiceIntent);
    }

    private void setupPermissions()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                String[] permissionsWeNeed = new String[]{
                        Manifest.permission.SEND_SMS
                        , Manifest.permission.READ_SMS
                        , Manifest.permission.RECEIVE_SMS
                        , Manifest.permission.ACCESS_NETWORK_STATE
                        , Manifest.permission.INTERNET
                        , Manifest.permission.RECEIVE_BOOT_COMPLETED
                };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_SEND_MSG_REQUEST_CODE);
            }
            else
            {
                // Otherwise, permissions were granted and we are ready to go!
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void initSSL()
    {
        try
        {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e)
        {
            Log.e(LOG_TAG, "Error configuring SSL", e);
        }
    }

    public void saveSettings(View button)
    {
        EditText txtBotId = findViewById(R.id.botId);
        EditText txtChatId = findViewById(R.id.chatId);

        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("botId", txtBotId.getText().toString());
        editor.putString("chatId", txtChatId.getText().toString());
        editor.apply();

        if(txtBotId.getText().toString().isEmpty() || txtChatId.getText().toString().isEmpty())
        {
            Toaster.show(getBaseContext(), "Service can not be started");
            stopFwdService();
            Log.i(LOG_TAG, "Not all data has been given. Service can not be started");
        }
        else
        {
            Toaster.show(getBaseContext(), "Settings were saved. Service started");
            startFwdService();
            Log.i(LOG_TAG, "Service was started");
        }
    }
}


