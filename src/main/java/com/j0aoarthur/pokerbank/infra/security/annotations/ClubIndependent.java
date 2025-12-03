package com.j0aoarthur.pokerbank.infra.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar métodos (geralmente em Services)
 * que devem ser executados FORA do contexto de um tenant.
 * Isso desabilita o filtro de tenant para esta operação.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClubIndependent {
}
