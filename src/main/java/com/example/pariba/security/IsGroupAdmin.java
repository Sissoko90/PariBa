package com.example.pariba.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation personnalisée pour vérifier que l'utilisateur est admin du groupe
 * Utilise une expression SpEL pour vérifier dynamiquement le rôle dans group_memberships
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@groupSecurityService.isGroupAdmin(authentication.principal.username, #paymentId)")
public @interface IsGroupAdmin {
}
