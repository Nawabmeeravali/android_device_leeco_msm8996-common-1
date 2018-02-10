package com.stericson.RootTools.execution;

import android.content.Context;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Shell {
    private static Shell customShell = null;
    private static String error = "";
    public static boolean isExecuting = false;
    public static boolean isReading = false;
    private static Shell rootShell = null;
    private static Shell shell = null;
    private static int shellTimeout = 25000;
    private static final String token = "F*D^W@#FGF";
    private boolean close = false;
    private final List<Command> commands = new ArrayList();
    private final BufferedReader in;
    private Runnable input = new C10191();
    private boolean isCleaning = false;
    private int maxCommands = 1000;
    private final OutputStreamWriter out;
    private Runnable output = new C10213();
    private final Process proc;
    private int read = 0;
    private int totalExecuted = 0;
    private int totalRead = 0;
    private int write = 0;

    class C10191 implements Runnable {
        C10191() {
        }

        public void run() {
            while (true) {
                try {
                    synchronized (Shell.this.commands) {
                        while (!Shell.this.close && Shell.this.write >= Shell.this.commands.size()) {
                            Shell.isExecuting = false;
                            Shell.this.commands.wait();
                        }
                    }
                    if (Shell.this.write >= Shell.this.maxCommands) {
                        while (Shell.this.read != Shell.this.write) {
                            RootTools.log("Waiting for read and write to catch up before cleanup.");
                        }
                        Shell.this.cleanCommands();
                    }
                    if (Shell.this.write < Shell.this.commands.size()) {
                        Shell.isExecuting = true;
                        Command cmd = (Command) Shell.this.commands.get(Shell.this.write);
                        cmd.startExecution();
                        RootTools.log("Executing: " + cmd.getCommand());
                        Shell.this.out.write(cmd.getCommand());
                        Shell.this.out.write("\necho F*D^W@#FGF " + Shell.this.totalExecuted + " $?\n");
                        Shell.this.out.flush();
                        Shell.this.write = Shell.this.write + 1;
                        Shell.this.totalExecuted = Shell.this.totalExecuted + 1;
                    } else if (Shell.this.close) {
                        Shell.isExecuting = false;
                        Shell.this.out.write("\nexit 0\n");
                        Shell.this.out.flush();
                        RootTools.log("Closing shell");
                        Shell.this.write = 0;
                        Shell.this.closeQuietly(Shell.this.out);
                        return;
                    }
                } catch (IOException e) {
                    try {
                        RootTools.log(e.getMessage(), 2, e);
                        return;
                    } finally {
                        Shell.this.write = 0;
                        Shell.this.closeQuietly(Shell.this.out);
                    }
                } catch (InterruptedException e2) {
                    RootTools.log(e2.getMessage(), 2, e2);
                    Shell.this.write = 0;
                    Shell.this.closeQuietly(Shell.this.out);
                    return;
                }
            }
        }
    }

    class C10202 extends Thread {
        C10202() {
        }

        public void run() {
            synchronized (Shell.this.commands) {
                Shell.this.commands.notifyAll();
            }
        }
    }

    class C10213 implements Runnable {
        C10213() {
        }

        public void run() {
            Command command = null;
            while (!Shell.this.close) {
                try {
                    Shell.isReading = false;
                    String line = Shell.this.in.readLine();
                    Shell.isReading = true;
                    if (line == null) {
                        break;
                    }
                    if (command == null) {
                        if (Shell.this.read < Shell.this.commands.size()) {
                            command = (Command) Shell.this.commands.get(Shell.this.read);
                        } else if (Shell.this.close) {
                            break;
                        }
                    }
                    int pos = line.indexOf(Shell.token);
                    if (pos == -1) {
                        command.output(command.id, line);
                    }
                    if (pos > 0) {
                        command.output(command.id, line.substring(0, pos));
                    }
                    if (pos >= 0) {
                        String[] fields = line.substring(pos).split(" ");
                        if (fields.length >= 2 && fields[1] != null) {
                            int id = 0;
                            try {
                                id = Integer.parseInt(fields[1]);
                            } catch (NumberFormatException e) {
                            }
                            int exitCode = -1;
                            try {
                                exitCode = Integer.parseInt(fields[2]);
                            } catch (NumberFormatException e2) {
                            }
                            if (id == Shell.this.totalRead) {
                                command.setExitCode(exitCode);
                                command.commandFinished();
                                command = null;
                                Shell.this.read = Shell.this.read + 1;
                                Shell.this.totalRead = Shell.this.totalRead + 1;
                            }
                        }
                    } else {
                        continue;
                    }
                } catch (IOException e3) {
                    RootTools.log(e3.getMessage(), 2, e3);
                    return;
                }
            }
            RootTools.log("Read all output");
            try {
                Shell.this.proc.waitFor();
                Shell.this.proc.destroy();
            } catch (Exception e4) {
            }
            Shell.this.closeQuietly(Shell.this.out);
            Shell.this.closeQuietly(Shell.this.in);
            RootTools.log("Shell destroyed");
            while (Shell.this.read < Shell.this.commands.size()) {
                if (command == null) {
                    command = (Command) Shell.this.commands.get(Shell.this.read);
                }
                command.terminated("Unexpected Termination.");
                command = null;
                Shell.this.read = Shell.this.read + 1;
            }
            Shell.this.read = 0;
        }
    }

    protected static class Worker extends Thread {
        public int exit;
        public BufferedReader in;
        public OutputStreamWriter out;
        public Process proc;

        private Worker(Process proc, BufferedReader in, OutputStreamWriter out) {
            this.exit = -911;
            this.proc = proc;
            this.in = in;
            this.out = out;
        }

        public void run() {
            try {
                this.out.write("echo Started\n");
                this.out.flush();
                while (true) {
                    String line = this.in.readLine();
                    if (line == null) {
                        throw new EOFException();
                    } else if (!"".equals(line)) {
                        if ("Started".equals(line)) {
                            this.exit = 1;
                            setShellOom();
                            return;
                        }
                        Shell.error = "unkown error occured.";
                    }
                }
            } catch (IOException e) {
                this.exit = -42;
                if (e.getMessage() != null) {
                    Shell.error = e.getMessage();
                } else {
                    Shell.error = "RootAccess denied?.";
                }
            }
        }

        private void setShellOom() {
            try {
                Field field;
                Class<?> processClass = this.proc.getClass();
                try {
                    field = processClass.getDeclaredField("pid");
                } catch (NoSuchFieldException e) {
                    field = processClass.getDeclaredField("id");
                }
                field.setAccessible(true);
                this.out.write("(echo -17 > /proc/" + ((Integer) field.get(this.proc)).intValue() + "/oom_adj) &> /dev/null\n");
                this.out.write("(echo -17 > /proc/$$/oom_adj) &> /dev/null\n");
                this.out.flush();
            } catch (Exception e2) {
            }
        }
    }

    private Shell(String cmd) throws IOException, TimeoutException, RootDeniedException {
        RootTools.log("Starting shell: " + cmd);
        this.proc = new ProcessBuilder(new String[]{cmd}).redirectErrorStream(true).start();
        this.in = new BufferedReader(new InputStreamReader(this.proc.getInputStream(), "UTF-8"));
        this.out = new OutputStreamWriter(this.proc.getOutputStream(), "UTF-8");
        Worker worker = new Worker(this.proc, this.in, this.out);
        worker.start();
        try {
            worker.join((long) shellTimeout);
            if (worker.exit == -911) {
                try {
                    this.proc.destroy();
                } catch (Exception e) {
                }
                closeQuietly(this.in);
                closeQuietly(this.out);
                throw new TimeoutException(error);
            } else if (worker.exit == -42) {
                try {
                    this.proc.destroy();
                } catch (Exception e2) {
                }
                closeQuietly(this.in);
                closeQuietly(this.out);
                throw new RootDeniedException("Root Access Denied");
            } else {
                Thread si = new Thread(this.input, "Shell Input");
                si.setPriority(5);
                si.start();
                Thread so = new Thread(this.output, "Shell Output");
                so.setPriority(5);
                so.start();
            }
        } catch (InterruptedException e3) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw new TimeoutException();
        }
    }

    public Command add(Command command) throws IOException {
        if (this.close) {
            throw new IllegalStateException("Unable to add commands to a closed shell");
        }
        do {
        } while (this.isCleaning);
        this.commands.add(command);
        notifyThreads();
        return command;
    }

    public void useCWD(Context context) throws IOException, TimeoutException, RootDeniedException {
        add(new CommandCapture(-1, false, "cd " + context.getApplicationInfo().dataDir));
    }

    private void cleanCommands() {
        this.isCleaning = true;
        int toClean = Math.abs(this.maxCommands - (this.maxCommands / 4));
        RootTools.log("Cleaning up: " + toClean);
        for (int i = 0; i < toClean; i++) {
            this.commands.remove(0);
        }
        this.read = this.commands.size() - 1;
        this.write = this.commands.size() - 1;
        this.isCleaning = false;
    }

    private void closeQuietly(Reader input) {
        if (input != null) {
            try {
                input.close();
            } catch (Exception e) {
            }
        }
    }

    private void closeQuietly(Writer output) {
        if (output != null) {
            try {
                output.close();
            } catch (Exception e) {
            }
        }
    }

    public void close() throws IOException {
        if (this == rootShell) {
            rootShell = null;
        } else if (this == shell) {
            shell = null;
        } else if (this == customShell) {
            customShell = null;
        }
        synchronized (this.commands) {
            this.close = true;
            notifyThreads();
        }
    }

    public static void closeCustomShell() throws IOException {
        if (customShell != null) {
            customShell.close();
        }
    }

    public static void closeRootShell() throws IOException {
        if (rootShell != null) {
            rootShell.close();
        }
    }

    public static void closeShell() throws IOException {
        if (shell != null) {
            shell.close();
        }
    }

    public static void closeAll() throws IOException {
        closeShell();
        closeRootShell();
        closeCustomShell();
    }

    public int getCommandQueuePosition(Command cmd) {
        return this.commands.indexOf(cmd);
    }

    public String getCommandQueuePositionString(Command cmd) {
        return "Command is in position " + getCommandQueuePosition(cmd) + " currently executing command at position " + this.write;
    }

    public static Shell getOpenShell() {
        if (customShell != null) {
            return customShell;
        }
        if (rootShell != null) {
            return rootShell;
        }
        return shell;
    }

    public static boolean isShellOpen() {
        if (shell == null) {
            return false;
        }
        return true;
    }

    public static boolean isCustomShellOpen() {
        if (customShell == null) {
            return false;
        }
        return true;
    }

    public static boolean isRootShellOpen() {
        if (rootShell == null) {
            return false;
        }
        return true;
    }

    public static boolean isAnyShellOpen() {
        if (shell == null && rootShell == null && customShell == null) {
            return false;
        }
        return true;
    }

    protected void notifyThreads() {
        new C10202().start();
    }

    public static void runRootCommand(Command command) throws IOException, TimeoutException, RootDeniedException {
        startRootShell().add(command);
    }

    public static void runCommand(Command command) throws IOException, TimeoutException {
        startShell().add(command);
    }

    public static Shell startRootShell() throws IOException, TimeoutException, RootDeniedException {
        return startRootShell(20000, 3);
    }

    public static Shell startRootShell(int timeout) throws IOException, TimeoutException, RootDeniedException {
        return startRootShell(timeout, 3);
    }

    public static Shell startRootShell(int timeout, int retry) throws IOException, TimeoutException, RootDeniedException {
        shellTimeout = timeout;
        if (rootShell == null) {
            RootTools.log("Starting Root Shell!");
            String cmd = "su";
            int retries = 0;
            while (rootShell == null) {
                try {
                    rootShell = new Shell(cmd);
                } catch (IOException e) {
                    int retries2 = retries + 1;
                    if (retries >= retry) {
                        RootTools.log("IOException, could not start shell");
                        throw e;
                    }
                    retries = retries2;
                }
            }
        } else {
            RootTools.log("Using Existing Root Shell!");
        }
        return rootShell;
    }

    public static Shell startCustomShell(String shellPath) throws IOException, TimeoutException, RootDeniedException {
        return startCustomShell(shellPath, 20000);
    }

    public static Shell startCustomShell(String shellPath, int timeout) throws IOException, TimeoutException, RootDeniedException {
        shellTimeout = timeout;
        if (customShell == null) {
            RootTools.log("Starting Custom Shell!");
            customShell = new Shell(shellPath);
        } else {
            RootTools.log("Using Existing Custom Shell!");
        }
        return customShell;
    }

    public static Shell startShell() throws IOException, TimeoutException {
        return startShell(20000);
    }

    public static Shell startShell(int timeout) throws IOException, TimeoutException {
        shellTimeout = timeout;
        try {
            if (shell == null) {
                RootTools.log("Starting Shell!");
                shell = new Shell("/system/bin/sh");
            } else {
                RootTools.log("Using Existing Shell!");
            }
            return shell;
        } catch (RootDeniedException e) {
            throw new IOException();
        }
    }
}
