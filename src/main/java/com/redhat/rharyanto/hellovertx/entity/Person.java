package com.redhat.rharyanto.hellovertx.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    public enum Sex {
        MALE, FEMALE
    }

    private String name;
    private int age;
    private Sex sex;
}
