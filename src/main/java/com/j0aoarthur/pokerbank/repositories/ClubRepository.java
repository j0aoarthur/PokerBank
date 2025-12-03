package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByPublicCode(String publicCode);
}
