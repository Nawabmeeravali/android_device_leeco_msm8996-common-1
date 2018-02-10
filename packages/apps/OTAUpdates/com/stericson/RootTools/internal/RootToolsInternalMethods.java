package com.stericson.RootTools.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import com.stericson.RootTools.Constants;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.containers.Mount;
import com.stericson.RootTools.containers.Permissions;
import com.stericson.RootTools.containers.Symlink;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

public final class RootToolsInternalMethods {
    protected RootToolsInternalMethods() {
    }

    public static void getInstance() {
        RootTools.setRim(new RootToolsInternalMethods());
    }

    public ArrayList<Symlink> getSymLinks() throws IOException {
        Throwable th;
        LineNumberReader lnr = null;
        FileReader fr = null;
        try {
            FileReader fr2 = new FileReader("/data/local/symlinks.txt");
            try {
                LineNumberReader lnr2 = new LineNumberReader(fr2);
                try {
                    ArrayList<Symlink> symlink = new ArrayList();
                    while (true) {
                        String line = lnr2.readLine();
                        if (line != null) {
                            RootTools.log(line);
                            String[] fields = line.split(" ");
                            symlink.add(new Symlink(new File(fields[fields.length - 3]), new File(fields[fields.length - 1])));
                        } else {
                            try {
                                break;
                            } catch (Exception e) {
                            }
                        }
                    }
                    fr2.close();
                    try {
                        lnr2.close();
                    } catch (Exception e2) {
                    }
                    return symlink;
                } catch (Throwable th2) {
                    th = th2;
                    fr = fr2;
                    lnr = lnr2;
                    try {
                        fr.close();
                    } catch (Exception e3) {
                    }
                    try {
                        lnr.close();
                    } catch (Exception e4) {
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fr = fr2;
                fr.close();
                lnr.close();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            fr.close();
            lnr.close();
            throw th;
        }
    }

    public Permissions getPermissions(String line) {
        String rawPermissions = line.split(" ")[0];
        if (rawPermissions.length() != 10 || ((rawPermissions.charAt(0) != '-' && rawPermissions.charAt(0) != 'd' && rawPermissions.charAt(0) != 'l') || ((rawPermissions.charAt(1) != '-' && rawPermissions.charAt(1) != 'r') || (rawPermissions.charAt(2) != '-' && rawPermissions.charAt(2) != 'w')))) {
            return null;
        }
        RootTools.log(rawPermissions);
        Permissions permissions = new Permissions();
        permissions.setType(rawPermissions.substring(0, 1));
        RootTools.log(permissions.getType());
        permissions.setUserPermissions(rawPermissions.substring(1, 4));
        RootTools.log(permissions.getUserPermissions());
        permissions.setGroupPermissions(rawPermissions.substring(4, 7));
        RootTools.log(permissions.getGroupPermissions());
        permissions.setOtherPermissions(rawPermissions.substring(7, 10));
        RootTools.log(permissions.getOtherPermissions());
        StringBuilder finalPermissions = new StringBuilder();
        finalPermissions.append(parseSpecialPermissions(rawPermissions));
        finalPermissions.append(parsePermissions(permissions.getUserPermissions()));
        finalPermissions.append(parsePermissions(permissions.getGroupPermissions()));
        finalPermissions.append(parsePermissions(permissions.getOtherPermissions()));
        permissions.setPermissions(Integer.parseInt(finalPermissions.toString()));
        return permissions;
    }

    public int parsePermissions(String permission) {
        int tmp;
        if (permission.charAt(0) == 'r') {
            tmp = 4;
        } else {
            tmp = 0;
        }
        RootTools.log("permission " + tmp);
        RootTools.log("character " + permission.charAt(0));
        if (permission.charAt(1) == 'w') {
            tmp += 2;
        } else {
            tmp += 0;
        }
        RootTools.log("permission " + tmp);
        RootTools.log("character " + permission.charAt(1));
        if (permission.charAt(2) == 'x') {
            tmp++;
        } else {
            tmp += 0;
        }
        RootTools.log("permission " + tmp);
        RootTools.log("character " + permission.charAt(2));
        return tmp;
    }

    public int parseSpecialPermissions(String permission) {
        int tmp = 0;
        if (permission.charAt(2) == 's') {
            tmp = 0 + 4;
        }
        if (permission.charAt(5) == 's') {
            tmp += 2;
        }
        if (permission.charAt(8) == 't') {
            tmp++;
        }
        RootTools.log("special permissions " + tmp);
        return tmp;
    }

    public boolean copyFile(String source, String destination, boolean remountAsRw, boolean preserveFileAttributes) {
        CommandCapture commandCapture = null;
        boolean result = true;
        if (remountAsRw) {
            try {
                RootTools.remount(destination, "RW");
            } catch (Exception e) {
                e = e;
                e.printStackTrace();
                result = false;
                if (commandCapture != null) {
                    return result;
                }
                return commandCapture.getExitCode() != 0;
            }
        }
        CommandCapture command;
        if (checkUtil("cp")) {
            RootTools.log("cp command is available!");
            if (preserveFileAttributes) {
                command = new CommandCapture(0, false, "cp -fp " + source + " " + destination);
                try {
                    Shell.startRootShell().add(command);
                    commandWait(command);
                    if (command.getExitCode() == 0) {
                        result = true;
                    } else {
                        result = false;
                    }
                    commandCapture = command;
                } catch (Exception e2) {
                    Exception e3;
                    e3 = e2;
                    commandCapture = command;
                    e3.printStackTrace();
                    result = false;
                    if (commandCapture != null) {
                        return result;
                    }
                    if (commandCapture.getExitCode() != 0) {
                    }
                }
            }
            command = new CommandCapture(0, false, "cp -f " + source + " " + destination);
            Shell.startRootShell().add(command);
            commandWait(command);
            result = command.getExitCode() == 0;
            commandCapture = command;
        } else if (checkUtil("busybox") && hasUtil("cp", "busybox")) {
            RootTools.log("busybox cp command is available!");
            if (preserveFileAttributes) {
                command = new CommandCapture(0, false, "busybox cp -fp " + source + " " + destination);
                Shell.startRootShell().add(command);
                commandWait(command);
                commandCapture = command;
            } else {
                command = new CommandCapture(0, false, "busybox cp -f " + source + " " + destination);
                Shell.startRootShell().add(command);
                commandWait(command);
                commandCapture = command;
            }
        } else if (checkUtil("cat")) {
            RootTools.log("cp is not available, use cat!");
            int filePermission = -1;
            if (preserveFileAttributes) {
                filePermission = getFilePermissionsSymlinks(source).getPermissions();
            }
            command = new CommandCapture(0, false, "cat " + source + " > " + destination);
            Shell.startRootShell().add(command);
            commandWait(command);
            if (preserveFileAttributes) {
                commandCapture = new CommandCapture(0, false, "chmod " + filePermission + " " + destination);
                Shell.startRootShell().add(commandCapture);
                commandWait(commandCapture);
            } else {
                commandCapture = command;
            }
        } else {
            result = false;
        }
        if (remountAsRw) {
            RootTools.remount(destination, "RO");
        }
        if (commandCapture != null) {
            return result;
        }
        if (commandCapture.getExitCode() != 0) {
        }
    }

    public boolean checkUtil(String util) {
        if (RootTools.findBinary(util)) {
            List<String> binaryPaths = new ArrayList();
            binaryPaths.addAll(RootTools.lastFoundBinaryPaths);
            for (String path : binaryPaths) {
                Permissions permissions = RootTools.getFilePermissionsSymlinks(path + "/" + util);
                if (permissions != null) {
                    String permission;
                    if (Integer.toString(permissions.getPermissions()).length() > 3) {
                        permission = Integer.toString(permissions.getPermissions()).substring(1);
                    } else {
                        permission = Integer.toString(permissions.getPermissions());
                    }
                    if (permission.equals("755") || permission.equals("777") || permission.equals("775")) {
                        RootTools.utilPath = path + "/" + util;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean deleteFileOrDirectory(String target, boolean remountAsRw) {
        boolean result = true;
        if (remountAsRw) {
            try {
                RootTools.remount(target, "RW");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        CommandCapture command;
        if (hasUtil("rm", "toolbox")) {
            RootTools.log("rm command is available!");
            command = new CommandCapture(0, false, "rm -r " + target);
            Shell.startRootShell().add(command);
            commandWait(command);
            if (command.getExitCode() != 0) {
                RootTools.log("target not exist or unable to delete file");
                result = false;
            }
        } else if (checkUtil("busybox") && hasUtil("rm", "busybox")) {
            RootTools.log("busybox cp command is available!");
            command = new CommandCapture(0, false, "busybox rm -rf " + target);
            Shell.startRootShell().add(command);
            commandWait(command);
            if (command.getExitCode() != 0) {
                RootTools.log("target not exist or unable to delete file");
                result = false;
            }
        }
        if (!remountAsRw) {
            return result;
        }
        RootTools.remount(target, "RO");
        return result;
    }

    public boolean exists(String file) {
        final List<String> result = new ArrayList();
        CommandCapture command = new CommandCapture(0, false, new String[]{"ls " + file}) {
            public void output(int arg0, String arg1) {
                RootTools.log(arg1);
                result.add(arg1);
            }
        };
        try {
            if (Shell.isAnyShellOpen()) {
                Shell.getOpenShell().add(command);
                commandWait(command);
            } else {
                Shell.startShell().add(command);
                commandWait(command);
            }
            for (String line : result) {
                if (line.trim().equals(file)) {
                    return true;
                }
            }
            try {
                RootTools.closeShell(false);
            } catch (Exception e) {
            }
            result.clear();
            try {
                Shell.startRootShell().add(command);
                commandWait(command);
                List<String> final_result = new ArrayList();
                final_result.addAll(result);
                for (String line2 : final_result) {
                    if (line2.trim().equals(file)) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e2) {
                return false;
            }
        } catch (Exception e3) {
            return false;
        }
    }

    public void fixUtil(String util, String utilPath) {
        try {
            RootTools.remount("/system", "rw");
            if (RootTools.findBinary(util)) {
                CommandCapture command;
                List<String> paths = new ArrayList();
                paths.addAll(RootTools.lastFoundBinaryPaths);
                for (String path : paths) {
                    command = new CommandCapture(0, false, utilPath + " rm " + path + "/" + util);
                    Shell.startRootShell().add(command);
                    commandWait(command);
                }
                command = new CommandCapture(0, false, utilPath + " ln -s " + utilPath + " /system/bin/" + util, utilPath + " chmod 0755 /system/bin/" + util);
                Shell.startRootShell().add(command);
                commandWait(command);
            }
            RootTools.remount("/system", "ro");
        } catch (Exception e) {
        }
    }

    public boolean fixUtils(String[] utils) throws Exception {
        for (String util : utils) {
            if (!checkUtil(util)) {
                if (checkUtil("busybox")) {
                    if (hasUtil(util, "busybox")) {
                        fixUtil(util, RootTools.utilPath);
                    }
                } else if (!checkUtil("toolbox")) {
                    return false;
                } else {
                    if (hasUtil(util, "toolbox")) {
                        fixUtil(util, RootTools.utilPath);
                    }
                }
            }
        }
        return true;
    }

    public boolean findBinary(String binaryName) {
        boolean found = false;
        RootTools.lastFoundBinaryPaths.clear();
        final List<String> list = new ArrayList();
        String[] places = new String[]{"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};
        RootTools.log("Checking for " + binaryName);
        try {
            for (final String path : places) {
                final String str = binaryName;
                CommandCapture cc = new CommandCapture(0, false, new String[]{"stat " + path + binaryName}) {
                    public void commandOutput(int id, String line) {
                        if (line.contains("File: ") && line.contains(str)) {
                            list.add(path);
                            RootTools.log(str + " was found here: " + path);
                        }
                        RootTools.log(line);
                    }
                };
                RootTools.getShell(false).add(cc);
                commandWait(cc);
            }
            found = !list.isEmpty();
        } catch (Exception e) {
            RootTools.log(binaryName + " was not found, more information MAY be available with Debugging on.");
        }
        if (!found) {
            RootTools.log("Trying second method");
            for (String where : places) {
                if (RootTools.exists(where + binaryName)) {
                    RootTools.log(binaryName + " was found here: " + where);
                    list.add(where);
                    found = true;
                } else {
                    RootTools.log(binaryName + " was NOT found here: " + where);
                }
            }
        }
        if (!found) {
            RootTools.log("Trying third method");
            try {
                List<String> paths = RootTools.getPath();
                if (paths != null) {
                    for (String path2 : paths) {
                        if (RootTools.exists(path2 + "/" + binaryName)) {
                            RootTools.log(binaryName + " was found here: " + path2);
                            list.add(path2);
                            found = true;
                        } else {
                            RootTools.log(binaryName + " was NOT found here: " + path2);
                        }
                    }
                }
            } catch (Exception e2) {
                RootTools.log(binaryName + " was not found, more information MAY be available with Debugging on.");
            }
        }
        Collections.reverse(list);
        RootTools.lastFoundBinaryPaths.addAll(list);
        return found;
    }

    public List<String> getBusyBoxApplets(String path) throws Exception {
        if (path != null && !path.endsWith("/") && !path.equals("")) {
            path = path + "/";
        } else if (path == null) {
            throw new Exception("Path is null, please specifiy a path");
        }
        final List<String> results = new ArrayList();
        CommandCapture command = new CommandCapture(3, false, new String[]{path + "busybox --list"}) {
            public void output(int id, String line) {
                if (id == 3 && !line.trim().equals("") && !line.trim().contains("not found")) {
                    results.add(line);
                }
            }
        };
        Shell.startRootShell().add(command);
        commandWait(command);
        return results;
    }

    public String getBusyBoxVersion(String path) {
        if (!(path.equals("") || path.endsWith("/"))) {
            path = path + "/";
        }
        RootTools.log("Getting BusyBox Version");
        InternalVariables.busyboxVersion = "";
        try {
            CommandCapture command = new CommandCapture(4, false, path + "busybox") {
                public void output(int id, String line) {
                    if (id == 4 && line.startsWith("BusyBox") && InternalVariables.busyboxVersion.equals("")) {
                        InternalVariables.busyboxVersion = line.split(" ")[1];
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);
            return InternalVariables.busyboxVersion;
        } catch (Exception e) {
            RootTools.log("BusyBox was not found, more information MAY be available with Debugging on.");
            return "";
        }
    }

    public long getConvertedSpace(String spaceStr) {
        double multiplier = 1.0d;
        try {
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while (i < spaceStr.length()) {
                char c = spaceStr.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    sb.append(spaceStr.charAt(i));
                    i++;
                } else if (c == 'm' || c == 'M') {
                    multiplier = 1024.0d;
                    return (long) Math.ceil(Double.valueOf(sb.toString()).doubleValue() * multiplier);
                } else {
                    if (c == 'g' || c == 'G') {
                        multiplier = 1048576.0d;
                    }
                    return (long) Math.ceil(Double.valueOf(sb.toString()).doubleValue() * multiplier);
                }
            }
            return (long) Math.ceil(Double.valueOf(sb.toString()).doubleValue() * multiplier);
        } catch (Exception e) {
            return -1;
        }
    }

    public String getInode(String file) {
        try {
            CommandCapture command = new CommandCapture(5, false, "/data/local/ls -i " + file) {
                public void output(int id, String line) {
                    if (id == 5 && !line.trim().equals("") && Character.isDigit(line.trim().substring(0, 1).toCharArray()[0])) {
                        InternalVariables.inode = line.trim().split(" ")[0];
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);
            return InternalVariables.inode;
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isAccessGiven() {
        try {
            RootTools.log("Checking for Root access");
            InternalVariables.accessGiven = false;
            CommandCapture command = new CommandCapture(2, false, "id") {
                public void output(int id, String line) {
                    if (id == 2) {
                        for (String userid : new HashSet(Arrays.asList(line.split(" ")))) {
                            RootTools.log(userid);
                            if (userid.toLowerCase().contains("uid=0")) {
                                InternalVariables.accessGiven = true;
                                RootTools.log("Access Given");
                                break;
                            }
                        }
                        if (!InternalVariables.accessGiven) {
                            RootTools.log("Access Denied?");
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);
            if (InternalVariables.accessGiven) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isNativeToolsReady(int nativeToolsId, Context context) {
        RootTools.log("Preparing Native Tools");
        InternalVariables.nativeToolsReady = false;
        try {
            Installer installer = new Installer(context);
            if (installer.isBinaryInstalled("nativetools")) {
                InternalVariables.nativeToolsReady = true;
            } else {
                InternalVariables.nativeToolsReady = installer.installBinary(nativeToolsId, "nativetools", "700");
            }
            return InternalVariables.nativeToolsReady;
        } catch (IOException ex) {
            if (!RootTools.debugMode) {
                return false;
            }
            ex.printStackTrace();
            return false;
        }
    }

    public Permissions getFilePermissionsSymlinks(String file) {
        Permissions permissions = null;
        RootTools.log("Checking permissions for " + file);
        if (!RootTools.exists(file)) {
            return permissions;
        }
        RootTools.log(file + " was found.");
        try {
            CommandCapture command = new CommandCapture(1, false, "ls -l " + file, "busybox ls -l " + file, "/system/bin/failsafe/toolbox ls -l " + file, "toolbox ls -l " + file) {
                public void output(int id, String line) {
                    if (id == 1) {
                        String symlink_final = "";
                        if (line.split(" ")[0].length() == 10) {
                            RootTools.log("Line " + line);
                            try {
                                String[] symlink = line.split(" ");
                                if (symlink[symlink.length - 2].equals("->")) {
                                    RootTools.log("Symlink found.");
                                    symlink_final = symlink[symlink.length - 1];
                                }
                            } catch (Exception e) {
                            }
                            try {
                                InternalVariables.permissions = RootToolsInternalMethods.this.getPermissions(line);
                                if (InternalVariables.permissions != null) {
                                    InternalVariables.permissions.setSymlink(symlink_final);
                                }
                            } catch (Exception e2) {
                                RootTools.log(e2.getMessage());
                            }
                        }
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);
            return InternalVariables.permissions;
        } catch (Exception e) {
            RootTools.log(e.getMessage());
            return permissions;
        }
    }

    public ArrayList<Mount> getMounts() throws Exception {
        Throwable th;
        Shell shell = RootTools.getShell(true);
        CommandCapture cmd = new CommandCapture(0, false, "cat /proc/mounts > /data/local/RootToolsMounts", "chmod 0777 /data/local/RootToolsMounts");
        shell.add(cmd);
        commandWait(cmd);
        LineNumberReader lnr = null;
        FileReader fr = null;
        try {
            FileReader fr2 = new FileReader("/data/local/RootToolsMounts");
            try {
                LineNumberReader lnr2 = new LineNumberReader(fr2);
                try {
                    ArrayList<Mount> mounts = new ArrayList();
                    while (true) {
                        String line = lnr2.readLine();
                        if (line == null) {
                            break;
                        }
                        RootTools.log(line);
                        String[] fields = line.split(" ");
                        mounts.add(new Mount(new File(fields[0]), new File(fields[1]), fields[2], fields[3]));
                    }
                    InternalVariables.mounts = mounts;
                    if (InternalVariables.mounts != null) {
                        ArrayList<Mount> arrayList = InternalVariables.mounts;
                        try {
                            fr2.close();
                        } catch (Exception e) {
                            fr = fr2;
                        }
                        try {
                            lnr2.close();
                        } catch (Exception e2) {
                            lnr = lnr2;
                        }
                        return arrayList;
                    }
                    throw new Exception();
                } catch (Throwable th2) {
                    th = th2;
                    fr = fr2;
                    lnr = lnr2;
                    try {
                        fr.close();
                    } catch (Exception e3) {
                    }
                    try {
                        lnr.close();
                    } catch (Exception e4) {
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fr = fr2;
                fr.close();
                lnr.close();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            fr.close();
            lnr.close();
            throw th;
        }
    }

    public String getMountedAs(String path) throws Exception {
        InternalVariables.mounts = getMounts();
        if (InternalVariables.mounts != null) {
            Iterator i$ = InternalVariables.mounts.iterator();
            while (i$.hasNext()) {
                Mount mount = (Mount) i$.next();
                String mp = mount.getMountPoint().getAbsolutePath();
                if (mp.equals("/")) {
                    if (path.equals("/")) {
                        return (String) mount.getFlags().toArray()[0];
                    }
                } else if (path.equals(mp) || path.startsWith(mp + "/")) {
                    RootTools.log((String) mount.getFlags().toArray()[0]);
                    return (String) mount.getFlags().toArray()[0];
                }
            }
            throw new Exception();
        }
        throw new Exception();
    }

    public long getSpace(String path) {
        InternalVariables.getSpaceFor = path;
        boolean found = false;
        RootTools.log("Looking for Space");
        try {
            CommandCapture command = new CommandCapture(6, false, "df " + path) {
                public void output(int id, String line) {
                    if (id == 6 && line.contains(InternalVariables.getSpaceFor.trim())) {
                        InternalVariables.space = line.split(" ");
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);
        } catch (Exception e) {
        }
        if (InternalVariables.space != null) {
            RootTools.log("First Method");
            for (String spaceSearch : InternalVariables.space) {
                RootTools.log(spaceSearch);
                if (found) {
                    return getConvertedSpace(spaceSearch);
                }
                if (spaceSearch.equals("used,")) {
                    found = true;
                }
            }
            int count = 0;
            int targetCount = 3;
            RootTools.log("Second Method");
            if (InternalVariables.space[0].length() <= 5) {
                targetCount = 2;
            }
            for (String spaceSearch2 : InternalVariables.space) {
                RootTools.log(spaceSearch2);
                if (spaceSearch2.length() > 0) {
                    RootTools.log(spaceSearch2 + "Valid");
                    if (count == targetCount) {
                        return getConvertedSpace(spaceSearch2);
                    }
                    count++;
                }
            }
        }
        RootTools.log("Returning -1, space could not be determined.");
        return -1;
    }

    public String getSymlink(String file) {
        RootTools.log("Looking for Symlink for " + file);
        try {
            final List<String> results = new ArrayList();
            CommandCapture command = new CommandCapture(7, false, new String[]{"ls -l " + file}) {
                public void output(int id, String line) {
                    if (id == 7 && !line.trim().equals("")) {
                        results.add(line);
                    }
                }
            };
            Shell.startRootShell().add(command);
            commandWait(command);
            String[] symlink = ((String) results.get(0)).split(" ");
            if (symlink.length > 2 && symlink[symlink.length - 2].equals("->")) {
                RootTools.log("Symlink found.");
                String final_symlink = "";
                if (symlink[symlink.length - 1].equals("") || symlink[symlink.length - 1].contains("/")) {
                    return symlink[symlink.length - 1];
                }
                findBinary(symlink[symlink.length - 1]);
                if (RootTools.lastFoundBinaryPaths.size() > 0) {
                    return ((String) RootTools.lastFoundBinaryPaths.get(0)) + "/" + symlink[symlink.length - 1];
                }
                return symlink[symlink.length - 1];
            }
        } catch (Exception e) {
            if (RootTools.debugMode) {
                e.printStackTrace();
            }
        }
        RootTools.log("Symlink not found");
        return "";
    }

    public ArrayList<Symlink> getSymlinks(String path) throws Exception {
        if (checkUtil("find")) {
            CommandCapture command = new CommandCapture(0, false, "dd if=/dev/zero of=/data/local/symlinks.txt bs=1024 count=1", "chmod 0777 /data/local/symlinks.txt");
            Shell.startRootShell().add(command);
            commandWait(command);
            command = new CommandCapture(0, false, "find " + path + " -type l -exec ls -l {} \\; > /data/local/symlinks.txt");
            Shell.startRootShell().add(command);
            commandWait(command);
            InternalVariables.symlinks = getSymLinks();
            if (InternalVariables.symlinks != null) {
                return InternalVariables.symlinks;
            }
            throw new Exception();
        }
        throw new Exception();
    }

    public String getWorkingToolbox() {
        if (RootTools.checkUtil("busybox")) {
            return "busybox";
        }
        if (RootTools.checkUtil("toolbox")) {
            return "toolbox";
        }
        return "";
    }

    public boolean hasEnoughSpaceOnSdCard(long updateSize) {
        RootTools.log("Checking SDcard size and that it is mounted as RW");
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return false;
        }
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (updateSize < ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize())) {
            return true;
        }
        return false;
    }

    public boolean hasUtil(String util, String box) {
        InternalVariables.found = false;
        if (!box.endsWith("toolbox") && !box.endsWith("busybox")) {
            return false;
        }
        try {
            String str;
            String[] strArr = new String[1];
            if (box.endsWith("toolbox")) {
                str = box + " " + util;
            } else {
                str = box + " --list";
            }
            strArr[0] = str;
            final String str2 = box;
            final String str3 = util;
            CommandCapture command = new CommandCapture(0, false, strArr) {
                public void output(int id, String line) {
                    if (str2.endsWith("toolbox")) {
                        if (!line.contains("no such tool")) {
                            InternalVariables.found = true;
                        }
                    } else if (str2.endsWith("busybox") && line.contains(str3)) {
                        RootTools.log("Found util!");
                        InternalVariables.found = true;
                    }
                }
            };
            RootTools.getShell(true).add(command);
            commandWait(command);
            if (InternalVariables.found) {
                RootTools.log("Box contains " + util + " util!");
                return true;
            }
            RootTools.log("Box does not contain " + util + " util!");
            return false;
        } catch (Exception e) {
            RootTools.log(e.getMessage());
            return false;
        }
    }

    public boolean installBinary(Context context, int sourceId, String destName, String mode) {
        try {
            return new Installer(context).installBinary(sourceId, destName, mode);
        } catch (IOException ex) {
            if (RootTools.debugMode) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean isBinaryAvailable(Context context, String binaryName) {
        try {
            return new Installer(context).isBinaryInstalled(binaryName);
        } catch (IOException ex) {
            if (RootTools.debugMode) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean isAppletAvailable(String applet, String binaryPath) {
        try {
            for (String aplet : getBusyBoxApplets(binaryPath)) {
                if (aplet.equals(applet)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            RootTools.log(e.toString());
            return false;
        }
    }

    public boolean isProcessRunning(String processName) {
        RootTools.log("Checks if process is running: " + processName);
        InternalVariables.processRunning = false;
        try {
            final String str = processName;
            CommandCapture command = new CommandCapture(0, false, new String[]{"ps"}) {
                public void output(int id, String line) {
                    if (line.contains(str)) {
                        InternalVariables.processRunning = true;
                    }
                }
            };
            RootTools.getShell(true).add(command);
            commandWait(command);
        } catch (Exception e) {
            RootTools.log(e.getMessage());
        }
        return InternalVariables.processRunning;
    }

    public boolean killProcess(String processName) {
        Exception e;
        RootTools.log("Killing process " + processName);
        InternalVariables.pid_list = "";
        InternalVariables.processRunning = true;
        try {
            final String str = processName;
            CommandCapture command = new CommandCapture(0, false, new String[]{"ps"}) {
                public void output(int id, String line) {
                    if (line.contains(str)) {
                        Matcher psMatcher = InternalVariables.psPattern.matcher(line);
                        try {
                            if (psMatcher.find()) {
                                String pid = psMatcher.group(1);
                                InternalVariables.pid_list += " " + pid;
                                InternalVariables.pid_list = InternalVariables.pid_list.trim();
                                RootTools.log("Found pid: " + pid);
                                return;
                            }
                            RootTools.log("Matching in ps command failed!");
                        } catch (Exception e) {
                            RootTools.log("Error with regex!");
                            e.printStackTrace();
                        }
                    }
                }
            };
            RootTools.getShell(true).add(command);
            commandWait(command);
            if (InternalVariables.pid_list.equals("")) {
                return true;
            }
            try {
                CommandCapture command2 = new CommandCapture(0, false, "kill -9 " + InternalVariables.pid_list);
                try {
                    RootTools.getShell(true).add(command2);
                    commandWait(command2);
                    return true;
                } catch (Exception e2) {
                    e = e2;
                    command = command2;
                    RootTools.log(e.getMessage());
                    return false;
                }
            } catch (Exception e3) {
                e = e3;
                RootTools.log(e.getMessage());
                return false;
            }
        } catch (Exception e4) {
            RootTools.log(e4.getMessage());
        }
    }

    public void offerBusyBox(Activity activity) {
        RootTools.log("Launching Market for BusyBox");
        activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=stericson.busybox")));
    }

    public Intent offerBusyBox(Activity activity, int requestCode) {
        RootTools.log("Launching Market for BusyBox");
        Intent i = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=stericson.busybox"));
        activity.startActivityForResult(i, requestCode);
        return i;
    }

    public void offerSuperUser(Activity activity) {
        RootTools.log("Launching Market for SuperUser");
        activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.noshufou.android.su")));
    }

    public Intent offerSuperUser(Activity activity, int requestCode) {
        RootTools.log("Launching Market for SuperUser");
        Intent i = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.noshufou.android.su"));
        activity.startActivityForResult(i, requestCode);
        return i;
    }

    private void commandWait(Command cmd) throws Exception {
        while (!cmd.isFinished()) {
            RootTools.log(Constants.TAG, Shell.getOpenShell().getCommandQueuePositionString(cmd));
            synchronized (cmd) {
                try {
                    if (!cmd.isFinished()) {
                        cmd.wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!(cmd.isExecuting() || cmd.isFinished())) {
                Exception e2;
                if (!Shell.isExecuting && !Shell.isReading) {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is not executing and not reading! \n\n Command: " + cmd.getCommand());
                    e2 = new Exception();
                    e2.setStackTrace(Thread.currentThread().getStackTrace());
                    e2.printStackTrace();
                } else if (!Shell.isExecuting || Shell.isReading) {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is not reading! \n\n Command: " + cmd.getCommand());
                    e2 = new Exception();
                    e2.setStackTrace(Thread.currentThread().getStackTrace());
                    e2.printStackTrace();
                } else {
                    Log.e(Constants.TAG, "Waiting for a command to be executed in a shell that is executing but not reading! \n\n Command: " + cmd.getCommand());
                    e2 = new Exception();
                    e2.setStackTrace(Thread.currentThread().getStackTrace());
                    e2.printStackTrace();
                }
            }
        }
    }
}
