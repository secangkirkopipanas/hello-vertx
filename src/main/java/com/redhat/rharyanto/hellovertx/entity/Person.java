package com.redhat.rharyanto.hellovertx.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
@Data
@Entity
@NoArgsConstructor
public class Person implements Serializable {

    public enum Sex {
        MALE, FEMALE, NONE
    }

    public Person(String name, int age) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, Sex sex) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public Person(String name, int age, Sex sex, String country) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.country = country;
    }

    @Id
    @GeneratedValue
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    private Sex sex;

    private String country;
}
