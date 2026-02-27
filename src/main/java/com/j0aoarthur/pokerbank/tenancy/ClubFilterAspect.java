package com.j0aoarthur.pokerbank.tenancy;

import com.j0aoarthur.pokerbank.entities.BaseTenantEntity;
import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClubFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    // Intercepta antes de qualquer método em Repository
    @Before("target(org.springframework.data.repository.Repository)")
    public void beforeRepositoryMethod() {
        Long clubId = ClubContext.getCurrentClubId();

        if (clubId != null) {
            Session session = entityManager.unwrap(Session.class);

            session.enableFilter("clubFilter").setParameter("clubId", clubId);
        }
    }

    // Aspect para garantir que novas entidades recebam o tenantId
    @Before("target(org.springframework.data.repository.Repository) && " +
            "execution(* save(..)) && args(entity)")
    public void beforeSave(JoinPoint joinPoint, Object entity) {
        if (entity instanceof BaseTenantEntity) {
            Long clubId = ClubContext.getCurrentClubId();
            if (clubId != null && ((BaseTenantEntity) entity).getClubId() == null) {
                ((BaseTenantEntity) entity).setClubId(clubId);
            }
        }
    }
}
