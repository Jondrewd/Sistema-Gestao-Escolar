package com.api.gestaoescolar.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api.gestaoescolar.entities.User;
import com.api.gestaoescolar.repositories.UserRepository;


@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    public Page<User> findAll(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);
        return users;
    }

    public User insert(User obj) {
        return userRepository.save(obj);
    }

    public User update(Long id, User obj) {
        Optional<User> newObj = userRepository.findById(id);
        User user = newObj.get();
        updateUser(user, obj);
        return userRepository.save(user);
    }

    public void updateUser(User user, User obj) {
        user.setUsername(obj.getUsername());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        if (user != null) {
            List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
            .collect(Collectors.toList());
    
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
        } else {
            throw new UsernameNotFoundException("Não foi possível achar esse nome.");
        }
        
    }
}