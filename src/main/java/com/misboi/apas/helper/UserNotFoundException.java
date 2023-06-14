package com.misboi.apas.helper;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("User with this username not found in database !!");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
