package com.example.Spring_shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loginId;
    private String password;
    private String picture;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;


    private String role= "ROLE_USER";


    public User(String picture, String name, String email) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }



    public User update(String name, String picture){
        this.name=name;
        this.picture=picture;
        return this;
    }
}
