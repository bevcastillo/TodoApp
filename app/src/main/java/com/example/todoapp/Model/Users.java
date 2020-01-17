package com.example.todoapp.Model;

public class Users {
    int id;
    String name;
    int age;
    String passw;

    public Users() {
    }

    public Users(int id, String name, int age, String passw) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.passw = passw;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassw() {
        return passw;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }
}
