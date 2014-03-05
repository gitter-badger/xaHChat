package com.xah.chat.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Ryno
 * Date: 2013/10/20
 * Time: 3:31 PM
 */
public class JavaUtils {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getDeviceId(Context context) {
        String id = getUniqueID(context);
        if (id == null)
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return id;
    }

    private static String getUniqueID(Context context) {

        String telephonyDeviceId = "NoTelephonyId";
        String androidDeviceId = "NoAndroidId";

        // get telephony id
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyDeviceId = tm.getDeviceId();
            if (telephonyDeviceId == null) {
                telephonyDeviceId = "NoTelephonyId";
            }
        } catch (Exception e) {
        }

        // get internal android device id
        try {
            androidDeviceId = android.provider.Settings.Secure.getString(context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            if (androidDeviceId == null) {
                androidDeviceId = "NoAndroidId";
            }
        } catch (Exception e) {

        }

        // build up the uuid
        try {
            String id = getStringIntegerHexBlocks(androidDeviceId.hashCode())
                    + "-"
                    + getStringIntegerHexBlocks(telephonyDeviceId.hashCode());

            return id;
        } catch (Exception e) {
            return "0000-0000-1111-1111";
        }
    }

    public static String getStringIntegerHexBlocks(int value) {
        String result = "";
        String string = Integer.toHexString(value);

        int remain = 8 - string.length();
        char[] chars = new char[remain];
        Arrays.fill(chars, '0');
        string = new String(chars) + string;

        int count = 0;
        for (int i = string.length() - 1; i >= 0; i--) {
            count++;
            result = string.substring(i, i + 1) + result;
            if (count == 4) {
                result = "-" + result;
                count = 0;
            }
        }

        if (result.startsWith("-")) {
            result = result.substring(1, result.length());
        }

        return result;
    }
}
