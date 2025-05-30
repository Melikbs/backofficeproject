package com.example.backofficeproject.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
@Setter
@Getter
public class ProfileDto {
    private String firstName;
    private String lastName;
    private int age;
    private String address;
    private LocalDate birthDate;

    public ProfileDto(String firstName, String lastName, int age, String address, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName =lastName;
        this.age = age;
        this.address = address;
        this.birthDate = birthDate;

    }
}
