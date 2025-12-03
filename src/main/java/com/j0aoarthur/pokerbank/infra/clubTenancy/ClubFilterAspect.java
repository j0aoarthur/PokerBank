package com.j0aoarthur.pokerbank.infra.clubTenancy;

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

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ClubFilterAspect.class);

    // Intercepta ANTES de qualquer método em qualquer classe dentro do pacote de repositórios
    @Before("execution(* com.j0aoarthur.pokerbank.repositories.*.*(..))")
    public void beforeRepositoryMethod(JoinPoint joinPoint) {
        Long clubId = ClubContext.getCurrentClubId();

        if (clubId != null) {
            Session session = entityManager.unwrap(Session.class);

            session.enableFilter("clubFilter").setParameter("clubId", clubId);
        }

        // Também precisamos aplicar o tenantId ao salvar/atualizar
        // Esta é uma lógica mais complexa, geralmente feita no service ou
        // usando @PrePersist/@PreUpdate (veja nota abaixo).
        // Por hora, focamos na LEITURA.
    }

    // Bônus: Aspecto para garantir que novas entidades recebam o tenantId
    @Before("execution(* org.springframework.data.jpa.repository.JpaRepository.save*(..)) && args(entity)")
    public void beforeSave(JoinPoint joinPoint, Object entity) {
        if (entity instanceof BaseTenantEntity) {
            Long clubId = ClubContext.getCurrentClubId();
            if (clubId != null && ((BaseTenantEntity) entity).getClubId() == null) {
                ((BaseTenantEntity) entity).setClubId(clubId);
            }
        }
    }
}
