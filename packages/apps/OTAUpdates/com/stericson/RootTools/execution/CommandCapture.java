package com.stericson.RootTools.execution;

import com.stericson.RootTools.RootTools;

public class CommandCapture extends Command {
    private StringBuilder sb = new StringBuilder();

    public CommandCapture(int id, String... command) {
        super(id, command);
    }

    public CommandCapture(int id, boolean handlerEnabled, String... command) {
        super(id, handlerEnabled, command);
    }

    public CommandCapture(int id, int timeout, String... command) {
        super(id, timeout, command);
    }

    public void commandOutput(int id, String line) {
        this.sb.append(line).append('\n');
        RootTools.log("Command", "ID: " + id + ", " + line);
    }

    public void commandTerminated(int id, String reason) {
    }

    public void commandCompleted(int id, int exitcode) {
    }

    public String toString() {
        return this.sb.toString();
    }
}
