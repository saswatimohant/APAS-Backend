package com.misboi.apas.controller;

import com.misboi.apas.entities.Role;
import com.misboi.apas.entities.User;
import com.misboi.apas.entities.UserRole;
import com.misboi.apas.helper.UserFoundException;
import com.misboi.apas.repository.UserRepository;
import com.misboi.apas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // creating user
    @PostMapping("/")
    public User createUser(@RequestBody User user) throws Exception {

        user.setProfile("default.png");

        // encoding passowrd with bcrypt

        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

        Set<UserRole> roles = new HashSet<>();
        Role role = new Role();
        role.setRoleId(45L);
        role.setRoleName("NORMAL");

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        roles.add(userRole);
        return this.userService.createUser(user,roles);
    }

    // getting user details
    @GetMapping("/{username}")
    public User getUser(@PathVariable("username")String username)
    {
        return this.userService.getUser(username);

    }

    // to get all the users
    @GetMapping("/listAll")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // delete user by id
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId")Long userId)
    {
        this.userService.deleteUser(userId);
    }

//    // update user by username
//    @PutMapping("/{userName")
//    public User updateUser(@PathVariable("userName")String userName)
//    {
//
//    }
//    @PutMapping("/update/{userId}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails){
//    	
//    }

    @ExceptionHandler(UserFoundException.class)
    public ResponseEntity<?> exceptionHandler(UserFoundException ex) {
        return ResponseEntity.ok(ex.getMessage());
    }
}
