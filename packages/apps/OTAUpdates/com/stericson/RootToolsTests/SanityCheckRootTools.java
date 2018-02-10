package com.stericson.RootToolsTests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.os.StrictMode.VmPolicy;
import android.support.v4.media.TransportMediator;
import android.widget.ScrollView;
import android.widget.TextView;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.containers.Permissions;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.JavaCommandCapture;
import com.stericson.RootTools.execution.Shell;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SanityCheckRootTools extends Activity {
    private ProgressDialog mPDialog;
    private ScrollView mScrollView;
    private TextView mTextView;

    class C10311 implements Runnable {
        C10311() {
        }

        public void run() {
            SanityCheckRootTools.this.mScrollView.fullScroll(TransportMediator.KEYCODE_MEDIA_RECORD);
        }
    }

    private class SanityCheckThread extends Thread {
        private Handler mHandler;

        public SanityCheckThread(Context context, Handler handler) {
            this.mHandler = handler;
        }

        public void run() {
            visualUpdate(1, null);
            visualUpdate(4, "Testing getPath");
            visualUpdate(3, "[ getPath ]\n");
            try {
                for (String path : RootTools.getPath()) {
                    visualUpdate(3, path + " k\n\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            visualUpdate(4, "Testing A ton of commands");
            visualUpdate(3, "[ Ton of Commands ]\n");
            for (int i = 0; i < 100; i++) {
                RootTools.exists("/system/xbin/busybox");
            }
            visualUpdate(4, "Testing Find Binary");
            boolean result = RootTools.isRootAvailable();
            visualUpdate(3, "[ Checking Root ]\n");
            visualUpdate(3, result + " k\n\n");
            visualUpdate(4, "Testing file exists");
            visualUpdate(3, "[ Checking Exists() ]\n");
            visualUpdate(3, RootTools.exists("/system/sbin/[") + " k\n\n");
            visualUpdate(4, "Testing Is Access Given");
            result = RootTools.isAccessGiven();
            visualUpdate(3, "[ Checking for Access to Root ]\n");
            visualUpdate(3, result + " k\n\n");
            visualUpdate(4, "Testing Remount");
            result = RootTools.remount("/system", "rw");
            visualUpdate(3, "[ Remounting System as RW ]\n");
            visualUpdate(3, result + " k\n\n");
            visualUpdate(4, "Testing CheckUtil");
            visualUpdate(3, "[ Checking busybox is setup ]\n");
            visualUpdate(3, RootTools.checkUtil("busybox") + " k\n\n");
            visualUpdate(4, "Testing getBusyBoxVersion");
            visualUpdate(3, "[ Checking busybox version ]\n");
            visualUpdate(3, RootTools.getBusyBoxVersion("/system/bin/") + " k\n\n");
            try {
                visualUpdate(4, "Testing fixUtils");
                visualUpdate(3, "[ Checking Utils ]\n");
                visualUpdate(3, RootTools.fixUtils(new String[]{"ls", "rm", "ln", "dd", "chmod", "mount"}) + " k\n\n");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                visualUpdate(4, "Testing getSymlink");
                visualUpdate(3, "[ Checking [[ for symlink ]\n");
                visualUpdate(3, RootTools.getSymlink("/system/bin/[[") + " k\n\n");
            } catch (Exception e22) {
                e22.printStackTrace();
            }
            visualUpdate(4, "Testing getInode");
            visualUpdate(3, "[ Checking Inodes ]\n");
            visualUpdate(3, RootTools.getInode("/system/bin/busybox") + " k\n\n");
            visualUpdate(4, "Testing GetBusyBoxapplets");
            try {
                visualUpdate(3, "[ Getting all available Busybox applets ]\n");
                for (String applet : RootTools.getBusyBoxApplets("/data/data/stericson.busybox.donate/files/bb")) {
                    visualUpdate(3, applet + " k\n\n");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            visualUpdate(4, "Testing getFilePermissionsSymlinks");
            Permissions permissions = RootTools.getFilePermissionsSymlinks("/system/bin/busybox");
            visualUpdate(3, "[ Checking busybox permissions and symlink ]\n");
            if (permissions != null) {
                visualUpdate(3, "Symlink: " + permissions.getSymlink() + " k\n\n");
                visualUpdate(3, "Group Permissions: " + permissions.getGroupPermissions() + " k\n\n");
                visualUpdate(3, "Owner Permissions: " + permissions.getOtherPermissions() + " k\n\n");
                visualUpdate(3, "Permissions: " + permissions.getPermissions() + " k\n\n");
                visualUpdate(3, "Type: " + permissions.getType() + " k\n\n");
                visualUpdate(3, "User Permissions: " + permissions.getUserPermissions() + " k\n\n");
            } else {
                visualUpdate(3, "Permissions == null k\n\n");
            }
            visualUpdate(4, "JAVA");
            visualUpdate(3, "[ Running some Java code ]\n");
            try {
                Shell shell = RootTools.getShell(true);
                Shell shell2 = shell;
                shell2.add(new JavaCommandCapture(43, false, SanityCheckRootTools.this, "com.stericson.RootToolsTests.NativeJavaClass") {
                    public void commandOutput(int id, String line) {
                        super.commandOutput(id, line);
                        SanityCheckThread.this.visualUpdate(3, line + "\n");
                    }
                });
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            visualUpdate(4, "Testing df");
            long spaceValue = RootTools.getSpace("/data");
            visualUpdate(3, "[ Checking /data partition size]\n");
            visualUpdate(3, spaceValue + "k\n\n");
            try {
                RootTools.getShell(true).add(new CommandCapture(42, false, "find /") {
                    boolean _catch = false;

                    public void commandOutput(int id, String line) {
                        super.commandOutput(id, line);
                        if (this._catch) {
                            RootTools.log("CAUGHT!!!");
                        }
                    }

                    public void commandTerminated(int id, String reason) {
                        synchronized (SanityCheckRootTools.this) {
                            this._catch = true;
                            SanityCheckThread.this.visualUpdate(4, "All tests complete.");
                            SanityCheckThread.this.visualUpdate(2, null);
                            try {
                                RootTools.closeAllShells();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    public void commandCompleted(int id, int exitCode) {
                        synchronized (SanityCheckRootTools.this) {
                            this._catch = true;
                            SanityCheckThread.this.visualUpdate(4, "All tests complete.");
                            SanityCheckThread.this.visualUpdate(2, null);
                            try {
                                RootTools.closeAllShells();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } catch (Exception e32) {
                e32.printStackTrace();
            }
        }

        private void visualUpdate(int action, String text) {
            Message msg = this.mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("action", action);
            bundle.putString("text", text);
            msg.setData(bundle);
            this.mHandler.sendMessage(msg);
        }
    }

    private class TestHandler extends Handler {
        public static final String ACTION = "action";
        public static final int ACTION_DISPLAY = 3;
        public static final int ACTION_HIDE = 2;
        public static final int ACTION_PDISPLAY = 4;
        public static final int ACTION_SHOW = 1;
        public static final String TEXT = "text";

        private TestHandler() {
        }

        public void handleMessage(Message msg) {
            int action = msg.getData().getInt("action");
            String text = msg.getData().getString("text");
            switch (action) {
                case 1:
                    SanityCheckRootTools.this.mPDialog.show();
                    SanityCheckRootTools.this.mPDialog.setMessage("Running Root Library Tests...");
                    return;
                case 2:
                    if (text != null) {
                        SanityCheckRootTools.this.print(text);
                    }
                    SanityCheckRootTools.this.mPDialog.hide();
                    return;
                case 3:
                    SanityCheckRootTools.this.print(text);
                    return;
                case 4:
                    SanityCheckRootTools.this.mPDialog.setMessage(text);
                    return;
                default:
                    return;
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        RootTools.debugMode = true;
        this.mTextView = new TextView(this);
        this.mTextView.setText("");
        this.mScrollView = new ScrollView(this);
        this.mScrollView.addView(this.mTextView);
        setContentView(this.mScrollView);
        String version = "?";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
        }
        print("SanityCheckRootTools v " + version + "\n\n");
        if (RootTools.isRootAvailable()) {
            print("Root found.\n");
        } else {
            print("Root not found");
        }
        try {
            Shell.startRootShell();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (TimeoutException e3) {
            print("[ TIMEOUT EXCEPTION! ]\n");
            e3.printStackTrace();
        } catch (RootDeniedException e4) {
            print("[ ROOT DENIED EXCEPTION! ]\n");
            e4.printStackTrace();
        }
        try {
            if (RootTools.isAccessGiven()) {
                this.mPDialog = new ProgressDialog(this);
                this.mPDialog.setCancelable(false);
                this.mPDialog.setProgressStyle(0);
                new SanityCheckThread(this, new TestHandler()).start();
                return;
            }
            print("ERROR: No root access to this device.\n");
        } catch (Exception e5) {
            print("ERROR: could not determine root access to this device.\n");
        }
    }

    protected void print(CharSequence text) {
        this.mTextView.append(text);
        this.mScrollView.post(new C10311());
    }
}
