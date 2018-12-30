package com.example.mountaineer.DocSpace;

public class Users {
    public String name, image, specialty;

    public Users(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Users(String name, String image, String specialty) {
        this.name = name;
        this.image = image;
        this.specialty = specialty;
    }
}
