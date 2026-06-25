package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.infra.exceptions.EntityNotFoundException;
import com.j0aoarthur.pokerbank.repositories.MemberRepository;
import com.j0aoarthur.pokerbank.services.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    
    @Mock
    private AuthContextService authContextService;
    
    private UUID validClaimToken;
    private Member mockMember;
    private Account mockUser;
    private Club mockClub;

    // Injeção do service que será utilizado
    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        validClaimToken = UUID.randomUUID();
        mockMember = new Member();
        mockMember.setId(1L);
        mockMember.setClaimToken(validClaimToken);
        mockMember.setName("Club Member Test");

        mockUser = new Account();
        mockUser.setId(1L);
        mockUser.setName("Account Test");
        mockUser.setEmail("Account@gmail.com");

        mockClub = new Club();
        mockClub.setId(1L);
        mockClub.setName("Club Test");

        mockMember.setClubId(mockClub.getId());
        mockMember.setClub(mockClub);
    }

    @Test
    void claimMember_Success_WhenTokenExists() {
        // Arrange
        when(authContextService.getCurrentUser()).thenReturn(mockUser);
        when(memberRepository.updateUserByClaimToken(validClaimToken, mockUser)).thenReturn(1);
        when(memberRepository.findAllByAccountId(mockUser.getId())).thenReturn(java.util.List.of(mockMember));

        // Act
        Member result = memberService.claimMember(validClaimToken);

        // Assert
        assertNotNull(result);
        assertEquals(mockMember.getId(), result.getId());
        verify(memberRepository, times(1)).updateUserByClaimToken(validClaimToken, mockUser);
    }

    @Test
    void claimMember_ThrowsException_WhenTokenNotFound() {
        // Arrange
        UUID invalidClaimToken = UUID.randomUUID();
        when(authContextService.getCurrentUser()).thenReturn(mockUser);
        when(memberRepository.updateUserByClaimToken(invalidClaimToken, mockUser)).thenReturn(0);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> memberService.claimMember(invalidClaimToken));

        assertTrue(exception.getMessage().contains("Jogador não encontrado com o claim token: " + invalidClaimToken));
        verify(memberRepository, times(1)).updateUserByClaimToken(invalidClaimToken, mockUser);
    }

    void claimMember_ThrowsException_WhenUserAlreadyMemberOfClub() {
        // Arrange
        when(memberRepository.findByClaimToken(validClaimToken)).thenReturn(Optional.of(mockMember));
        when(authContextService.getCurrentUser()).thenReturn(mockUser);
        when(memberRepository.findByAccountIdAndClubId(mockUser.getId(), mockClub.getId())).thenReturn(Optional.of(new Member()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> memberService.claimMember(validClaimToken));

        assertTrue(exception.getMessage().contains("O usuário já é um jogador deste clube."));

        verify(memberRepository, times(1)).findByClaimToken(validClaimToken);
        verify(memberRepository, times(1)).findByAccountIdAndClubId(mockUser.getId(), mockClub.getId());
    }

}
