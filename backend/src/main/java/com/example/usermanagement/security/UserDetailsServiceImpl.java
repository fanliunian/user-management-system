package com.example.usermanagement.security;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security用户详情服务实现
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsernameWithRoles(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userMapper.findByIdWithRoles(id);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + id);
        }

        return UserPrincipal.create(user);
    }
}