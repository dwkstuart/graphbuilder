package com.dwk.enterprise.graphbuilder.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Customer extends RulesData {

    public String firstName;
    public String lastName;
    public int age;

}
