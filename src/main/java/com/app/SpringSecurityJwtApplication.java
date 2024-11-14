package com.app;

import com.app.config.SecurityConfig;
import com.app.persistence.entity.PermissionEntity;
import com.app.persistence.entity.RolEntity;
import com.app.persistence.entity.RolEnum;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringSecurityJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}


	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			SecurityConfig securityConfig = new SecurityConfig();
			//crear permisos
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();

			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();
			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();
			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();
			PermissionEntity refactorPermission = PermissionEntity.builder()
					.name("REFACTOR")
					.build();

			//create roles
			RolEntity adminRol = RolEntity.builder()
					.rolEnum(RolEnum.ADMIN)
					.permissions(Set.of(createPermission, readPermission, updatePermission, deletePermission))
					.build();

			RolEntity userRol = RolEntity.builder()
					.rolEnum(RolEnum.USER)
					.permissions(Set.of(createPermission, readPermission))
					.build();

			RolEntity invitedRol = RolEntity.builder()
					.rolEnum(RolEnum.INVITED)
					.permissions(Set.of(readPermission))
					.build();

			RolEntity devRol = RolEntity.builder()
					.rolEnum(RolEnum.DEV)
					.permissions(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
					.build();

			//create users

			UserEntity userEder = UserEntity.builder()
					.name("Eder")
					.lastName("Melo")
					.email("eder@mail.com")
					.phone("3003422755")
					.password(securityConfig.passwordEncoder().encode("123456"))
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(adminRol))
					.build();

			UserEntity userErnesto = UserEntity.builder()
					.name("Ernesto")
					.lastName("salazar")
					.email("ernesto@mail.com")
					.phone("3003422756")
					.password(securityConfig.passwordEncoder().encode("123456"))
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(adminRol))
					.build();

			UserEntity userEdwin = UserEntity.builder()
					.name("Edwin")
					.lastName("Blanco")
					.email("edwin@mail.com")
					.phone("3003422757")
					.password(securityConfig.passwordEncoder().encode("123456"))
					.isEnabled(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(userRol))
					.build();

			userRepository.saveAll(List.of(userEder, userErnesto, userEdwin));
		};
	}
}
