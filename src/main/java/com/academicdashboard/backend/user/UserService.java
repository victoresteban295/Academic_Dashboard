package com.academicdashboard.backend.user;

import org.springframework.stereotype.Service;

import com.academicdashboard.backend.exception.ApiRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void usernameExist(String username) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByUsername(username).isPresent();

        if(exists) {
            throw new ApiRequestException("Username Is Already Taken");
        }
    }

    public void emailExist(String email) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByEmail(email).isPresent();

        if(exists) {
            throw new ApiRequestException("Email Is Already Taken");
        }
    }

    public void phoneExist(String phone) {
        //True = Exist || False == Doesn't Exist
        boolean exists = userRepository.findUserByPhone(phone).isPresent();

        if(exists) {
            throw new ApiRequestException("Phone Is Already Taken");
        }
    }
}
