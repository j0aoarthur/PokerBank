package com.j0aoarthur.pokerbank.tenancy;

import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClubFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    // Intercepta métodos de Repository e métodos/classes com @Transactional
    @Before("target(org.springframework.data.repository.Repository) || " +
            "@annotation(org.springframework.transaction.annotation.Transactional) || " +
            "@within(org.springframework.transaction.annotation.Transactional)")
    public void enableClubFilter() {
        Long clubId = ClubContext.getCurrentClubId();

        if (clubId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("clubFilter").setParameter("clubId", clubId);
        }
    }
}
