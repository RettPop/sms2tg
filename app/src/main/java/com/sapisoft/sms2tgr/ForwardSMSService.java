package com.sapisoft.sms2tgr;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ForwardSMSService extends Service
{
    private static final String LOG_TAG = "ForwardSMSService";

    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(action))
            {
                Log.i(LOG_TAG, "on receive," + intent.getAction());
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()))
                {
                    StringBuilder messageToSend = new StringBuilder("");
                    String address = "";
                    SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                    for (SmsMessage smsMessage : messages)
                    {
                        StringBuilder message = new StringBuilder(smsMessage.getMessageBody());
                        address = smsMessage.getOriginatingAddress();
                        messageToSend.append(message);
                    }
                    messageToSend.append("\n[from: " + address + "]");

                    Log.i(LOG_TAG, "message send:" + messageToSend);
                    String chat_id = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("chatId", "");
                    String botId = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("botId", "");
                    String url = Constants.getURL(context, botId);
                    StackMessages stack = new StackMessages(context);
                    sendToTelegramAPI(context, chat_id, messageToSend.toString(), url, stack);
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION))
        {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("SMS forwarding")
                    .setTicker("SMS forwarder")
                    .setContentText("Keep Me Alive")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }
        else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION))
        {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void sendToTelegramAPI(final Context context, final String chat_id, final String msg, final String url, final StackMessages stack)
    {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("VolleyError", "That didn't work!", error);
                        stack.addToStack(msg);
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("chat_id", chat_id);
                params.put("text", msg);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
