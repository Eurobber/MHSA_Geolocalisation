package com.ece.iceageophone.main.util;

public enum Command {

    GET_LOCATION("location"),
    VIBRATE("vibrate");

    private String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

}
