package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    @Query("SELECT p FROM ClubMember cm WHERE cm.id NOT IN (SELECT gp.clubMember.id FROM GamePlayer gp WHERE gp.game.id = :gameId) ORDER BY cm.name")
    List<ClubMember> findClubMembersNotInGameOrderByName(@Param("gameId") Long gameId);

    Optional<ClubMember> findByUserId(Long userId);
}
