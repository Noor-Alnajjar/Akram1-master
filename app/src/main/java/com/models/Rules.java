package com.models;

import java.io.Serializable;

public class Rules implements Serializable {

    String rule_id,name;

    public String getRule_id() {
        return rule_id;
    }

    public String getName() {
        return name;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
