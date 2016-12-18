package com.ece.iceageophone.main.util;

public enum Command {

    GET_LOCATION("location"),
    VIBRATE("vibrate"),
    RING("ring");

    private String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public static Command getCommand(String name) throws IllegalArgumentException {
        for (Command command : Command.values()) {
            if (name.equals(command.getCommandName())) {
                return command;
            }
        }
        throw new IllegalArgumentException();
    }

}
