package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.MemberRequestDTO;
import com.j0aoarthur.pokerbank.dtos.request.ClubRequestDTO;
import com.j0aoarthur.pokerbank.entities.Club;
import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.infra.context.AuthContextService;
import com.j0aoarthur.pokerbank.repositories.ClubRepository;
import com.j0aoarthur.pokerbank.services.impl.ClubServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubServiceTest {

    // Mocks das dependências do ClubService
    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberService membershipService;

    @Mock
    private AuthContextService authContextService;

    // Injeção do serviço que será testado
    @InjectMocks
    private ClubServiceImpl clubService;

    @Test
    void createClub_shouldSaveAndReturnClub() {
        // Arrange
        ClubRequestDTO clubRequestDTO = new ClubRequestDTO("Clube da Luta", "Descrição do clube");

        // Mock do usuário autenticado
        Account mockedUser = new Account();
        mockedUser.setId(1L);
        mockedUser.setName("Tyler Durden");
        when(authContextService.getCurrentUser()).thenReturn(mockedUser);

        // Mock do salvamento do clube
        when(clubRepository.save(any(Club.class))).thenAnswer(invocation -> {
            Club createdClub = invocation.getArgument(0);
            createdClub.setId(1L); // Simula a atribuição de ID pelo banco
            return createdClub;
        });

        // Mock da criação do membro (dono) via serviço de membros
        when(membershipService.createMember(any(MemberRequestDTO.class), any(Club.class)))
                .thenAnswer(invocation -> {
                    MemberRequestDTO dto = invocation.getArgument(0);
                    Club clubArg = invocation.getArgument(1);
                    Member savedMember = new Member();
                    savedMember.setName(dto.name());
                    savedMember.setRole(dto.role());
                    savedMember.setClub(clubArg);
                    savedMember.setAccount(mockedUser);
                    savedMember.setId(101L); // Simula a atribuição de ID pelo banco
                    return savedMember;
                });

        // Act
        Club createdClub = clubService.createClub(clubRequestDTO);

        // Assert
        assertThat(createdClub).isNotNull();
        assertThat(createdClub.getName()).isEqualTo("Clube da Luta");
        assertThat(createdClub.getId()).isEqualTo(1L);
        assertThat(createdClub.getDescription()).isEqualTo("Descrição do clube");

        // Argument Captor para capturar os objetos que foram passados para os mocks
        var clubCaptor = ArgumentCaptor.forClass(Club.class);
        var memberDtoCaptor = ArgumentCaptor.forClass(MemberRequestDTO.class);
        var memberClubCaptor = ArgumentCaptor.forClass(Club.class);

        // Verifica se os métodos de salvamento foram chamados
        verify(clubRepository, times(1)).save(clubCaptor.capture());
        verify(membershipService, times(1)).createMember(memberDtoCaptor.capture(), memberClubCaptor.capture());

        // Verifica os detalhes dos objetos capturados
        Club savedClub = clubCaptor.getValue();
        MemberRequestDTO savedMemberDto = memberDtoCaptor.getValue();

        // O jogador foi criado com os dados corretos?
        assertThat(memberClubCaptor.getValue()).isEqualTo(savedClub);
        assertThat(savedMemberDto.role()).isEqualTo(Role.OWNER);
        assertThat(savedMemberDto.name()).isEqualTo(mockedUser.getName());
    }

    // Testa se o método joinClub lança uma exceção quando o usuário já é membro do clube
    @Test
    void joinClub_shouldThrowExceptionIfUserAlreadyMember() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Clube do Poker");
        club.setDescription("Clube para amantes do poker");

        Account mockedUser = new Account();
        mockedUser.setId(1L);
        mockedUser.setName("Alice");

        when(clubRepository.findByPublicCode(club.getPublicCode())).thenReturn(Optional.of(club));
        when(authContextService.getCurrentUser()).thenReturn(mockedUser);
        when(membershipService.getMemberByUserAndClub(mockedUser.getId(), club.getId()))
                .thenReturn(Optional.of(new Member()));

        // Act & Assert: Verifica se a exceção é lançada corretamente
        assertThatThrownBy(() -> clubService.joinClub(club.getPublicCode()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário já é membro deste clube.");
    }

    @Test
    void updateClub_shouldUpdateAndReturnClub() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Clube Antigo");
        club.setDescription("Descrição Antiga");

        ClubRequestDTO updateDTO = new ClubRequestDTO("Clube Novo", "Descrição Nova");

        when(clubRepository.findById(club.getId())).thenReturn(Optional.of(club));
        when(clubRepository.save(any(Club.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Club updatedClub = clubService.updateClub(club.getId(), updateDTO);

        // Assert
        assertThat(updatedClub.getName()).isEqualTo("Clube Novo");
        assertThat(updatedClub.getDescription()).isEqualTo("Descrição Nova");
        verify(clubRepository, times(1)).save(club);
    }

    @Test
    void deleteClubById_shouldDeleteClub() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Clube a ser deletado");

        when(clubRepository.findById(club.getId())).thenReturn(Optional.of(club));
        doNothing().when(clubRepository).delete(club);

        // Act
        clubService.deleteClub(club.getId());

        // Assert
        verify(clubRepository, times(1)).delete(club);
    }

    @Test
    void removeMemberFromClub_shouldRemoveMember() {
        // Arrange
        Club club = new Club();
        club.setId(1L);
        club.setName("Clube do Poker");

        Member memberToRemove = new Member();
        memberToRemove.setId(101L);
        memberToRemove.setName("Alice");

        club.getMembers().add(memberToRemove);

        when(clubRepository.findById(club.getId())).thenReturn(Optional.of(club));
        when(membershipService.getMemberById(memberToRemove.getId())).thenReturn(memberToRemove);
        when(clubRepository.save(any(Club.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        clubService.removeMemberFromClub(club.getId(), memberToRemove.getId());

        // Assert
        assertThat(club.getMembers()).doesNotContain(memberToRemove);
        verify(clubRepository, times(1)).save(club);
    }
}
