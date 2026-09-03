package com.sistema.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitário responsável pela validação do campo E-mail no backend (Servlet/Model).
 * Segue as regras padrão de expressão regular para formato de e-mails válidos.
 */
public class EmailValidator {

    // Expressão regular padrão RFC 5322 para validação de e-mail
    private static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Valida se uma string de e-mail é nula, vazia ou possui formato inválido.
     * 
     * @param email String contendo o e-mail a ser validado.
     * @return true se o e-mail for válido, false caso contrário.
     */
    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email.trim());
        return matcher.matches();
    }
}