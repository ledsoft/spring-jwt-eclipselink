package com.github.ledsoft.demo.service.security;

import com.github.ledsoft.demo.repository.UserRepository;
import com.github.ledsoft.demo.security.model.DemoUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public DemoUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new DemoUserDetails(userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username + " not found.")));
    }
}
