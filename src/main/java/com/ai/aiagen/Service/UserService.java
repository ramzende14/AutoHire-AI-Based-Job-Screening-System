package com.ai.aiagen.Service;

import com.ai.aiagen.Entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);
    User saveUser(User user);
}
