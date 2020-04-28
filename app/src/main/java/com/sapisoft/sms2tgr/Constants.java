package com.sapisoft.sms2tgr;

import android.content.Context;

/**
 * Created by uuplusu on 20/06/2017.
 */
public class Constants
{
    public static final String unsent_msgs_stack = "DELAYED_STACK";
    public static final String pkg = "com.sapisoft.sms2tg";

    public interface ACTION
    {
        String MAIN_ACTION = "com.sapisoft.sms2tg.action.main";
        String STARTFOREGROUND_ACTION = "com.sapisoft.sms2tg.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.sapisoft.sms2tg.action.stopforeground";
    }

    public interface NOTIFICATION_ID
    {
        int FOREGROUND_SERVICE = 101;
    }

    public static String getURL(Context context, String bot_id)
    {
        String _bot_id = bot_id;
        if (_bot_id.equals("Default"))
        {
            _bot_id = bot_id;
        }
        String telegram_url = "https://api.telegram.org/bot" + _bot_id + "/sendMessage";
        return telegram_url;
    }

}
