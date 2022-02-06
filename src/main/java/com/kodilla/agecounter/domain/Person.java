package com.kodilla.agecounter.domain;

import java.time.LocalDate;

public class Person {

    private int id;
    private String name;
    private String lname;
    private LocalDate dateOfBirth;
    private int age;

    public Person() {
    }

    private Person(int id, String name, String lname, int age) {
        this.id = id;
        this.name = name;
        this.lname = lname;
        this.age = age;
    }

    public Person build(int id, String name, String lname, int age) {
        return new Person(id, name, lname, age);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return age;
    }


}
