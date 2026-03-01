package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.ClubMember;
import com.j0aoarthur.pokerbank.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    @Query("SELECT cm FROM ClubMember cm WHERE cm.id NOT IN (SELECT gp.clubMember.id FROM GamePlayer gp WHERE gp.game.id = :gameId) ORDER BY cm.name")
    List<ClubMember> findClubMembersNotInGameOrderByName(@Param("gameId") Long gameId);

    Optional<ClubMember> findByUserId(Long userId);

    List<ClubMember> findAllByUserId(Long userId);

    Optional<ClubMember> findByUserIdAndClubId(Long userId, Long clubId);

    Optional<ClubMember> findByClaimToken(UUID claimToken);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ClubMember cm SET cm.claimToken = null, cm.user = :user WHERE cm.claimToken = :claimToken AND cm.user IS NULL")
    Integer updateUserByClaimToken(@Param("claimToken") UUID claimToken, @Param("user") User user);
}
