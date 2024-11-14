package com.app.service;

import com.app.controller.dto.AuthCreateUserRequest;
import com.app.controller.dto.AuthLoginRequest;
import com.app.controller.dto.AuthResponse;
import com.app.persistence.entity.RolEntity;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.RolRepository;
import com.app.persistence.repository.UserRepository;
import com.app.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImp implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Override
    //this is converter of UserEntity in UserDetails
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // get user of db
        UserEntity user = userRepository.findUserByName(username).orElseThrow(()-> new UsernameNotFoundException("El Usuario "+username+" no existe!"));

        /* set a list SimpleGrantedAuthority the ROLES permited */

        //1. instance of list
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //2. extract roles of user and add authorityList
        user.getRoles().stream().forEach(rol -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(rol.getRolEnum().name()))));

        //3. extract permissions of user and add authorityList
        user.getRoles().stream().
                flatMap(rol -> rol.getPermissions().stream()).
                forEach(permission -> authorityList.add( new SimpleGrantedAuthority(permission.getName())));

        //4. create user type userDetails
        User userDetail = new User(user.getName(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNoExpired(),
                user.isCredentialNoExpired(),
                user.isAccountNoLocked(),
                authorityList);

        return userDetail;
    }


    public AuthResponse loginUser(AuthLoginRequest authLoginRequest){
    String username = authLoginRequest.username();
    String password = authLoginRequest.password();

    Authentication authentication = this.authenticate(username,password);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String accessToken = jwtUtils.createToken(authentication);
    AuthResponse authResponse = new AuthResponse(username,"user loged successfuly", accessToken,true);
    return authResponse;
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest){
        String username = authCreateUserRequest.name();
        String lastname = authCreateUserRequest.lastName();
        String email = authCreateUserRequest.email();
        String phone = authCreateUserRequest.phone();
        String password = authCreateUserRequest.password();
        List<String> roles = authCreateUserRequest.roleRequest().roleListName();

        Set<RolEntity> rolEntitySet= rolRepository.findRolByrolEnumIn(roles).stream().collect(Collectors.toSet());

        if (rolEntitySet.isEmpty()){
            throw new IllegalArgumentException("the role specified does no exist");
        }
        UserEntity userEntity = UserEntity.builder()
                .name(username)
                .lastName(lastname)
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .roles(rolEntitySet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        UserEntity userCreated = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userCreated.getRoles().forEach(
                rol ->  authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(rol.getRolEnum().name()))));

        userCreated.getRoles().stream().flatMap(
                rol-> rol.getPermissions().stream()).forEach(
                        permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        SecurityContext context = SecurityContextHolder.getContext();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getName(),userCreated.getPassword(),authorityList);
        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse  authResponse = new AuthResponse(userCreated.getName(),"user created successfuly",accessToken,true);
        return authResponse;
    }

    public Authentication authenticate(String username,String password){
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null){
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password!");
        }
        return new UsernamePasswordAuthenticationToken(username,userDetails.getPassword(),userDetails.getAuthorities());
    }


}
