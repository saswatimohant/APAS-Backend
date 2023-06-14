package com.misboi.apas.services.impl;

import com.misboi.apas.entities.User;
import com.misboi.apas.entities.UserRole;
import com.misboi.apas.helper.UserFoundException;
import com.misboi.apas.helper.UserNotFoundException;
import com.misboi.apas.repository.RoleRepository;
import com.misboi.apas.repository.UserRepository;
import com.misboi.apas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    // creating user
    @Override
    public User createUser(User user, Set<UserRole> userRoles) throws Exception {

       // to check that user exists or not already
        User local = this.userRepository.findByUsername(user.getUsername());
        if(local!=null)
        {
            System.out.println("User is already present");
            throw new UserFoundException();
        }
        else
        {
            // if not we will create a user
            for(UserRole ur:userRoles) // search for all the assigned roles
            {
                roleRepository.save(ur.getRole());
            }
            //every user is assigned a role
            user.getUserRoles().addAll(userRoles);
            local = this.userRepository.save(user);



        }

        return local;
    }

    // getting user by username
    @Override
    public User getUser(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public void deleteUser(Long userId) {
        this.userRepository.deleteById(userId);
    }

//    @Override
//    public User updateUser(String userName) {
//        return this.userRepository.
//    }
}
