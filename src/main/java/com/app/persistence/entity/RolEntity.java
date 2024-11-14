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
@Table(name = "roles")
public class RolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "rol_seq")
    @SequenceGenerator(name = "rol_sq",sequenceName = "rol_id_sq",allocationSize = 1)
    private Long id;
    @Column(name = "rol_name",length = 50,nullable = false)
    @Enumerated(EnumType.STRING)
    private RolEnum rolEnum;

    @ManyToMany(fetch =  FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "rol_permission",joinColumns = @JoinColumn(name = "rol_id"),inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permissions = new HashSet<>();


}
