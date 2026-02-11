package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.dtos.response.AuthResponse;
import com.j0aoarthur.pokerbank.entities.User;

public interface AuthService {
    AuthResponse generateTokens(String username);
    AuthResponse refreshTokens(String refreshToken);
    User createUser(AuthRequestDTO authRequestDTO);
    boolean verifyUserEmail(String verificationToken);
    void requestPasswordReset(String email);
    void resetPassword(NewPasswordDTO newPasswordDTO);
    AuthResponse selectClub(Long clubId);
    void logout(String refreshToken);
}
