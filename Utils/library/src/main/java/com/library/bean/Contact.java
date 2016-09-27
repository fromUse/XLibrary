package com.library.bean;

/**
 * Created by chen on 2016/9/19.
 */
public class Contact {

    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
