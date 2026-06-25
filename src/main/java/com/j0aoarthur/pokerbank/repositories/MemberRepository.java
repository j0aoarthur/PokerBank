package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.Member;
import com.j0aoarthur.pokerbank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT cm FROM Member cm WHERE cm.id NOT IN (SELECT gp.member.id FROM GameParticipant gp WHERE gp.game.id = :gameId) ORDER BY cm.name")
    List<Member> findMembersNotInGameOrderByName(@Param("gameId") Long gameId);

    Optional<Member> findByAccountId(Long accountId);

    List<Member> findAllByAccountId(Long accountId);

    Optional<Member> findByAccountIdAndClubId(Long accountId, Long clubId);

    Optional<Member> findByClaimToken(UUID claimToken);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member cm SET cm.claimToken = null, cm.account = :account WHERE cm.claimToken = :claimToken AND cm.account IS NULL")
    Integer updateUserByClaimToken(@Param("claimToken") UUID claimToken, @Param("account") Account account);
}
