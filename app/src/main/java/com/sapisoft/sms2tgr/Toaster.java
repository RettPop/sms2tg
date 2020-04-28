package com.sapisoft.sms2tgr;

import android.content.Context;
import android.widget.Toast;

public class Toaster
{
    static public void show(Context con, String str)
    {
        Toast.makeText(con, str, Toast.LENGTH_LONG).show();
    }
}
