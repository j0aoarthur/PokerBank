package com.j0aoarthur.pokerbank.interfaces;

import com.j0aoarthur.pokerbank.DTOs.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.DTOs.response.AuthResponse;
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
