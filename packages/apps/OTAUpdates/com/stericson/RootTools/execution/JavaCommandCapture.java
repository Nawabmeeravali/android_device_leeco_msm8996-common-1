package com.stericson.RootTools.execution;

import android.content.Context;
import com.stericson.RootTools.RootTools;

public class JavaCommandCapture extends Command {
    private StringBuilder sb = new StringBuilder();

    public JavaCommandCapture(int id, Context context, String... command) {
        super(id, true, context, command);
    }

    public JavaCommandCapture(int id, boolean handlerEnabled, Context context, String... command) {
        super(id, handlerEnabled, true, context, command);
    }

    public JavaCommandCapture(int id, int timeout, Context context, String... command) {
        super(id, timeout, true, context, command);
    }

    public void commandOutput(int id, String line) {
        this.sb.append(line).append('\n');
        RootTools.log("Command", "ID: " + id + ", " + line);
    }

    public void commandTerminated(int id, String reason) {
    }

    public void commandCompleted(int id, int exitCode) {
    }

    public String toString() {
        return this.sb.toString();
    }
}
