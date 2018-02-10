package com.stericson.RootTools.execution;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.stericson.RootTools.RootTools;
import java.io.IOException;

public abstract class Command {
    String[] command;
    Context context;
    boolean executing;
    ExecutionMonitor executionMonitor;
    int exitCode;
    boolean finished;
    boolean handlerEnabled;
    int id;
    boolean javaCommand;
    Handler mHandler;
    boolean terminated;
    int timeout;

    private class CommandHandler extends Handler {
        public static final String ACTION = "action";
        public static final int COMMAND_COMPLETED = 2;
        public static final int COMMAND_OUTPUT = 1;
        public static final int COMMAND_TERMINATED = 3;
        public static final String TEXT = "text";

        private CommandHandler() {
        }

        public void handleMessage(Message msg) {
            int action = msg.getData().getInt("action");
            String text = msg.getData().getString("text");
            switch (action) {
                case 1:
                    Command.this.commandOutput(Command.this.id, text);
                    return;
                case 2:
                    Command.this.commandCompleted(Command.this.id, Command.this.exitCode);
                    return;
                case 3:
                    Command.this.commandTerminated(Command.this.id, text);
                    return;
                default:
                    return;
            }
        }
    }

    private class ExecutionMonitor extends Thread {
        private ExecutionMonitor() {
        }

        public void run() {
            while (!Command.this.finished) {
                synchronized (Command.this) {
                    try {
                        Command.this.wait((long) Command.this.timeout);
                    } catch (InterruptedException e) {
                    }
                }
                if (!Command.this.finished) {
                    RootTools.log("Timeout Exception has occurred.");
                    Command.this.terminate("Timeout Exception");
                }
            }
        }
    }

    public abstract void commandCompleted(int i, int i2);

    public abstract void commandOutput(int i, String str);

    public abstract void commandTerminated(int i, String str);

    public Command(int id, String... command) {
        this.executionMonitor = null;
        this.mHandler = null;
        this.executing = false;
        this.command = new String[0];
        this.javaCommand = false;
        this.context = null;
        this.finished = false;
        this.terminated = false;
        this.handlerEnabled = true;
        this.exitCode = -1;
        this.id = 0;
        this.timeout = RootTools.default_Command_Timeout;
        this.command = command;
        this.id = id;
        createHandler(RootTools.handlerEnabled);
    }

    public Command(int id, boolean handlerEnabled, String... command) {
        this.executionMonitor = null;
        this.mHandler = null;
        this.executing = false;
        this.command = new String[0];
        this.javaCommand = false;
        this.context = null;
        this.finished = false;
        this.terminated = false;
        this.handlerEnabled = true;
        this.exitCode = -1;
        this.id = 0;
        this.timeout = RootTools.default_Command_Timeout;
        this.command = command;
        this.id = id;
        createHandler(handlerEnabled);
    }

    public Command(int id, int timeout, String... command) {
        this.executionMonitor = null;
        this.mHandler = null;
        this.executing = false;
        this.command = new String[0];
        this.javaCommand = false;
        this.context = null;
        this.finished = false;
        this.terminated = false;
        this.handlerEnabled = true;
        this.exitCode = -1;
        this.id = 0;
        this.timeout = RootTools.default_Command_Timeout;
        this.command = command;
        this.id = id;
        this.timeout = timeout;
        createHandler(RootTools.handlerEnabled);
    }

    public Command(int id, boolean javaCommand, Context context, String... command) {
        this(id, command);
        this.javaCommand = javaCommand;
        this.context = context;
    }

    public Command(int id, boolean handlerEnabled, boolean javaCommand, Context context, String... command) {
        this(id, handlerEnabled, command);
        this.javaCommand = javaCommand;
        this.context = context;
    }

    public Command(int id, int timeout, boolean javaCommand, Context context, String... command) {
        this(id, timeout, command);
        this.javaCommand = javaCommand;
        this.context = context;
    }

    protected void finishCommand() {
        this.executing = false;
        this.finished = true;
        notifyAll();
    }

    protected void commandFinished() {
        if (!this.terminated) {
            synchronized (this) {
                if (this.mHandler == null || !this.handlerEnabled) {
                    commandCompleted(this.id, this.exitCode);
                } else {
                    Message msg = this.mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("action", 2);
                    msg.setData(bundle);
                    this.mHandler.sendMessage(msg);
                }
                RootTools.log("Command " + this.id + " finished.");
                finishCommand();
            }
        }
    }

    private void createHandler(boolean handlerEnabled) {
        this.handlerEnabled = handlerEnabled;
        if (Looper.myLooper() == null || !handlerEnabled) {
            RootTools.log("CommandHandler not created");
            return;
        }
        RootTools.log("CommandHandler created");
        this.mHandler = new CommandHandler();
    }

    public String getCommand() {
        StringBuilder sb = new StringBuilder();
        if (this.javaCommand) {
            String filePath = this.context.getFilesDir().getPath();
            for (String str : this.command) {
                sb.append("dalvikvm -cp " + filePath + "/anbuild.dex" + " com.android.internal.util.WithFramework" + " com.stericson.RootTools.containers.RootClass " + str);
                sb.append('\n');
            }
        } else {
            for (String append : this.command) {
                sb.append(append);
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    public boolean isExecuting() {
        return this.executing;
    }

    public boolean isHandlerEnabled() {
        return this.handlerEnabled;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public int getExitCode() {
        return this.exitCode;
    }

    protected void setExitCode(int code) {
        synchronized (this) {
            this.exitCode = code;
        }
    }

    protected void startExecution() {
        this.executionMonitor = new ExecutionMonitor();
        this.executionMonitor.setPriority(1);
        this.executionMonitor.start();
        this.executing = true;
    }

    public void terminate(String reason) {
        try {
            Shell.closeAll();
            RootTools.log("Terminating all shells.");
            terminated(reason);
        } catch (IOException e) {
        }
    }

    protected void terminated(String reason) {
        synchronized (this) {
            if (this.mHandler == null || !this.handlerEnabled) {
                commandTerminated(this.id, reason);
            } else {
                Message msg = this.mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("action", 3);
                bundle.putString("text", reason);
                msg.setData(bundle);
                this.mHandler.sendMessage(msg);
            }
            RootTools.log("Command " + this.id + " did not finish because it was terminated. Termination reason: " + reason);
            setExitCode(-1);
            this.terminated = true;
            finishCommand();
        }
    }

    protected void output(int id, String line) {
        if (this.mHandler == null || !this.handlerEnabled) {
            commandOutput(id, line);
            return;
        }
        Message msg = this.mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putInt("action", 1);
        bundle.putString("text", line);
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
    }
}
