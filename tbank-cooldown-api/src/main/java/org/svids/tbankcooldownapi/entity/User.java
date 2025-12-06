package org.svids.tbankcooldownapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "nickname", length = 256)
    private String nickname;

    @Column(name = "about", length = Integer.MAX_VALUE)
    private String about;

    @Column(name = "auto_cooling")
    private boolean autoCooling;

}