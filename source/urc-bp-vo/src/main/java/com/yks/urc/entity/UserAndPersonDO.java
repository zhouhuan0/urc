package com.yks.urc.entity;

import java.io.Serializable;

public class UserAndPersonDO implements Serializable {

    private static final long serialVersionUID = -7017146890751241612L;
    public String personName;
    public String userName;

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
