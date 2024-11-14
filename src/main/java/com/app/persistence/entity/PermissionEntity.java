package com.app.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "permissions")
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_seq")
    @SequenceGenerator(name = "permission_seq",sequenceName = "permission_id_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true,nullable = false, updatable = false)
    private String name;

}
