package com.example.pariba.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * Annotation pour injecter l'ID de l'utilisateur actuellement authentifié
 * Utilise @AuthenticationPrincipal de Spring Security
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUserAnnotation {
}
