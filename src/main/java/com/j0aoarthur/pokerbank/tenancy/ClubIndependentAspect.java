package com.j0aoarthur.pokerbank.tenancy;

import com.j0aoarthur.pokerbank.infra.context.ClubContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ClubIndependentAspect {

    // Executa "ao redor" de qualquer método anotado com @ClubIndependent
    @Around("@annotation(com.j0aoarthur.pokerbank.tenancy.annotations.ClubIndependent)")
    public Object executeAsClubIndependent(ProceedingJoinPoint joinPoint) throws Throwable {

        // 1. Salva o contexto de clube atual (se houver)
        Long currentClubId = ClubContext.getCurrentClubId();

        // 2. Limpa o contexto para a execução do método
        ClubContext.clear();

        try {
            // 3. Executa o método
            // O ClubIndependentAspect verá o contexto como null e não aplicará o filtro
            return joinPoint.proceed();
        } finally {
            // 4. Restaura o contexto original após a execução
            if (currentClubId != null) {
                ClubContext.setCurrentClubId(currentClubId);
            }
        }
    }
}
