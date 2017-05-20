package com.digi.diary.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Anupama on 1/24/2017.
 */
public class CommonUtilities {
public static final String TEXT_FONT="text_font";
    public static final String TEXT_FONT_SIZE="text_font_size";

    public static int getTextFont(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(TEXT_FONT, 0);
    }

    public static void setTextFont(Context context,int font)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(TEXT_FONT, font).commit();
    }
    public static int getTextFontSize(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(TEXT_FONT_SIZE, 22);
    }

    public static void setTextFontSize(Context context,int fontsize)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(TEXT_FONT_SIZE, fontsize).commit();
    }
}
