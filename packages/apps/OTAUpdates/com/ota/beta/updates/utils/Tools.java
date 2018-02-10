package com.ota.beta.updates.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import com.stericson.RootTools.RootTools;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Tools implements Constants {
    public final String TAG = getClass().getSimpleName();

    public static void recovery(Context context) {
        rebootPhone(context, "recovery");
    }

    public static String shell(String cmd, boolean root) {
        String out = "";
        Iterator it = system(root ? getSuBin() : "sh", cmd).getStringArrayList("out").iterator();
        while (it.hasNext()) {
            out = new StringBuilder(String.valueOf(out)).append((String) it.next()).append("\n").toString();
        }
        return out;
    }

    public static void getRoot() {
        RootTools.isAccessGiven();
    }

    public static boolean isRootAvailable() {
        return RootTools.isRootAvailable();
    }

    private static void rebootPhone(Context context, String type) {
        try {
            ((PowerManager) context.getSystemService("power")).reboot("recovery");
        } catch (Exception e) {
            Log.e("Tools", "reboot '" + type + "' error: " + e.getMessage());
            shell("reboot " + type, true);
        }
    }

    private static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static String getSuBin() {
        if (new File("/system/xbin", "su").exists()) {
            return "/system/xbin/su";
        }
        if (RootTools.isRootAvailable()) {
            return "su";
        }
        return "sh";
    }

    private static Bundle system(String shell, String command) {
        ArrayList<String> res = new ArrayList();
        ArrayList<String> err = new ArrayList();
        boolean success = false;
        try {
            String read;
            Process process = Runtime.getRuntime().exec(shell);
            DataOutputStream STDIN = new DataOutputStream(process.getOutputStream());
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            Log.i(shell, command);
            STDIN.writeBytes(new StringBuilder(String.valueOf(command)).append("\n").toString());
            STDIN.flush();
            STDIN.writeBytes("exit\n");
            STDIN.flush();
            process.waitFor();
            if (process.exitValue() == MotionEventCompat.ACTION_MASK) {
                Log.e(shell, "SU was probably denied! Exit value is 255");
                err.add("SU was probably denied! Exit value is 255");
            }
            while (STDOUT.ready()) {
                read = STDOUT.readLine();
                Log.d(shell, read);
                res.add(read);
            }
            while (STDERR.ready()) {
                read = STDERR.readLine();
                Log.e(shell, read);
                err.add(read);
            }
            process.destroy();
            success = true;
            if (err.size() > 0) {
                success = false;
            }
        } catch (IOException e) {
            Log.e(shell, "IOException: " + e.getMessage());
            err.add("IOException: " + e.getMessage());
        } catch (InterruptedException e2) {
            Log.e(shell, "InterruptedException: " + e2.getMessage());
            err.add("InterruptedException: " + e2.getMessage());
        }
        Log.d(shell, "END");
        Bundle r = new Bundle();
        r.putBoolean("success", success);
        r.putString("cmd", command);
        r.putString("binary", shell);
        r.putStringArrayList("out", res);
        r.putStringArrayList("error", err);
        return r;
    }
}
