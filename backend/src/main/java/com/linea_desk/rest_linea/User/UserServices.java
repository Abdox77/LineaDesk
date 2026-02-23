package com.linea_desk.rest_linea.User;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;



@Service
public class UserServices {
  private final UserRepository userRepository;

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);
        return users;
    }

}
