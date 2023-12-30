package com.dwk.enterprise.graphbuilder.interfaces;

import lombok.Data;

@Data
public final class Customer extends RulesData {

    public String firstName;
    public String lastName;
    public int age;
}
