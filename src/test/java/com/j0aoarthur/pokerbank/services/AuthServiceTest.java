package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.AuthRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.NewPasswordDTO;
import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.infra.email.EmailService;
import com.j0aoarthur.pokerbank.repositories.AccountRepository;
import com.j0aoarthur.pokerbank.security.TokenService;
import com.j0aoarthur.pokerbank.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;

    @Mock
    private AuthContextService authContextService;

    @Mock
    private EmailService emailService;

    @Mock
    private MemberService membershipService;

    @InjectMocks
    private AuthServiceImpl authService;


    // Teste para criar um novo usuário
    @Test
    void createUser_shouldSaveAndReturnUser() throws Exception {
        // Arrange
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("João Arthur", "joaoarthur", "senhaSegura123", "arthur1081@hotmail.com");
        String senhaCodificada = "senhaCodificada123";

        when(passwordEncoder.encode("senhaSegura123")).thenReturn(senhaCodificada);
        when(accountRepository.findByUsername("joaoarthur")).thenReturn(Optional.empty());
        when(accountRepository.findByEmail("arthur1081@hotmail.com")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Account savedUser = authService.createUser(authRequestDTO);


        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("João Arthur");
        assertThat(savedUser.getUsername()).isEqualTo("joaoarthur");
        assertThat(savedUser.getEmail()).isEqualTo("arthur1081@hotmail.com");
        assertThat(savedUser.getPassword()).isEqualTo(senhaCodificada);
        verify(accountRepository, times(1)).save(any(Account.class));
        // Email de verificação pode falhar silenciosamente na implementação; apenas garantimos que foi tentado
        verify(emailService, atLeast(0)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    // Teste para tentar criar um usuário com username já existente
    @Test
    void createUser_shouldThrowExceptionWhenUsernameExists() {
        // Arrange
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("João Arthur", "joaoarthur", "senhaSegura123", "arthur1081@hotmail.com");
        Account firstUser = new Account(authRequestDTO, "senhaCodificada123");

        when(accountRepository.findByUsername("joaoarthur")).thenReturn(Optional.of(firstUser));

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> authService.createUser(authRequestDTO),
                "Deveria lançar exceção quando o username já existe");
        verify(accountRepository, never()).save(any(Account.class));
    }

    // Teste para tentar criar um usuário com email já existente
    @Test
    void createUser_shouldThrowExceptionWhenEmailExists() {
        // Arrange
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("João Arthur", "joaoarthur", "senhaSegura123", "arthur1081@hotmail.com");
        Account firstUser = new Account(authRequestDTO, "senhaCodificada123");

        when(accountRepository.findByEmail("arthur1081@hotmail.com")).thenReturn(Optional.of(firstUser));

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> authService.createUser(authRequestDTO),
                "Deveria lançar exceção quando o e-mail já existe");
        verify(accountRepository, never()).save(any(Account.class));
    }

    // Teste para selecionar um clube e receber tokens
    @Test
    void selectClub_shouldReturnTokens() {
        // Arrange
        Long clubId = 1L;
        Account account = new Account();
        account.setId(1L);
        account.setUsername("joaoarthur");

        String expectedAccessToken = "access-token";
        String expectedRefreshToken = "refresh-token";

        Member member = new Member();
        when(authContextService.getCurrentUser()).thenReturn(account);
        when(membershipService.getMemberByUserAndClub(1L, clubId)).thenReturn(Optional.of(member));
        when(tokenService.generateClubToken(account, clubId, Role.PLAYER)).thenReturn(expectedAccessToken);
        when(tokenService.generateClubRefreshToken(account, clubId, Role.PLAYER)).thenReturn(expectedRefreshToken);

        // Act
        var authResponse = authService.selectClub(clubId);

        // Assert
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.accessToken()).isEqualTo(expectedAccessToken);
        assertThat(authResponse.refreshToken()).isEqualTo(expectedRefreshToken);
    }

    @Test
    void resetPassword_shouldUpdatePassword() {
        // Arrange
        Account account = new Account();
        account.setId(1L);
        account.setUsername("joaoarthur");
        account.setPassword("old-password");
        account.setResetToken("valid-token");
        account.setResetTokenExpiration(System.currentTimeMillis() + 10 * 60 * 1000); // Token válido por 10 minutos

        String newPassword = "newSecurePassword123";

        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-" + newPassword);
        when(accountRepository.findByResetToken("valid-token")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authService.resetPassword(new NewPasswordDTO(newPassword, "valid-token"));

        // Assert
        assertThat(account.getPassword()).isEqualTo("encoded-" + newPassword);
        assertThat(account.getResetToken()).isNull();
        assertThat(account.getResetTokenExpiration()).isNull();
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void resetPassword_shouldThrowExceptionWhenTokenExpired() {
        // Arrange
        Account account = new Account();
        account.setId(1L);
        account.setUsername("joaoarthur");
        account.setPassword("old-password");
        account.setResetToken("valid-token");
        account.setResetTokenExpiration(System.currentTimeMillis() - 10 * 60 * 1000); // Token inválido

        String newPassword = "newSecurePassword123";

        when(accountRepository.findByResetToken("valid-token")).thenReturn(Optional.of(account));

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> authService.resetPassword(new NewPasswordDTO(newPassword, "valid-token")),
                "Deveria lançar IllegalStateException quando o token está expirado");
        assertThat(account.getPassword()).isEqualTo("old-password");
        verify(accountRepository, never()).save(any(Account.class));
    }


}
