package com.alifetvaci.credit.userservice.service;


import com.alifetvaci.credit.userservice.api.exception.ErrorCode;
import com.alifetvaci.credit.userservice.api.exception.GenericException;
import com.alifetvaci.credit.userservice.controller.request.LoginRequest;
import com.alifetvaci.credit.userservice.controller.request.RegisterRequest;
import com.alifetvaci.credit.userservice.controller.response.LoginResponse;
import com.alifetvaci.credit.userservice.delegate.auth.service.AuthService;
import com.alifetvaci.credit.userservice.repository.UserRepository;
import com.alifetvaci.credit.userservice.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthService authService;

    public void register(RegisterRequest request) {
        User user = User.builder().identificationNumber(request.getIdentificationNumber()).firstname(request.getFirstName()).lastname(request.getLastName()).password(passwordEncoder.encode(request.getPassword())).build();
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByIdentificationNumber(request.getIdentificationNumber()).orElseThrow(() ->GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".login user not found with identification number {0}", request.getIdentificationNumber() )
                .message(ErrorCode.USER_NOT_FOUND)
                .build());
        passwordEncoder.matches(request.getPassword(),user.getPassword());
        return LoginResponse.builder().token(authService.createToken(user)).build();
    }
}
