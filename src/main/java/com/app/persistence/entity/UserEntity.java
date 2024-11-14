package com.app.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_seq")
    @SequenceGenerator(name = "user_sq",sequenceName = "user_id_sq", allocationSize = 1)
    private Long id;
    @Column(length = 50,nullable = false)
    private String name;
    @Column(name = "last_name",length = 50,nullable = false)
    private String lastName;
    @Column(length = 100,nullable = false,unique = true)
    private String email;
    @Column(length = 20,unique = true,nullable = false)
    private String phone;
    @Column(nullable = false)
    private String password;
    @Column(name = "is_enable")
    private boolean isEnabled;
    @Column(name = "account_no_expired")
    private boolean accountNoExpired;
    @Column(name = "account_no_locked")
    private boolean accountNoLocked;
    @Column(name = "credential_no_expired")
    private boolean credentialNoExpired;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    // nombre de la tabla con las relaciones // nombre de la 1columna con la primera forikey //nombre de la 2columna con la primera forikey
    @JoinTable(name = "user_rol",joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<RolEntity> roles = new HashSet<>();

}
