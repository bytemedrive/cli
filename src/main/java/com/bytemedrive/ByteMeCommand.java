package com.bytemedrive;

public abstract class ByteMeCommand implements Runnable {

    protected final String username;
    protected final char[] password;

    public ByteMeCommand(){
        username = System.console().readLine("Username: ");
        password = System.console().readPassword("Password: ");
    }

}
