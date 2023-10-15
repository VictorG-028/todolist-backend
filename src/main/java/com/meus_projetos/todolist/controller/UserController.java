package com.meus_projetos.todolist.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meus_projetos.todolist.model.UserModel;
import com.meus_projetos.todolist.repository.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @GetMapping("/find")
    public ResponseEntity find(String username) {
        var user =  userRepository.findByUsername(username);
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .body(user);
    }
    
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserModel newUser) {
        var user = userRepository.findByUsername(newUser.getUsername());

        if (user != null) {
            String msg = "Usuário já existe!";
            System.out.println(msg);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(msg);
        }

        var hashedPass = BCrypt.withDefaults().hashToString(
            12, 
            newUser.getPassword().toCharArray()
        );
        newUser.setPassword(hashedPass);

        var userCreated = userRepository.save(newUser);
        System.out.println(userCreated.toString());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userCreated);
    }
}
