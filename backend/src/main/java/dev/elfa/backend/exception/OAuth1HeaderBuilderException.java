package dev.elfa.backend.exception;

public class OAuth1HeaderBuilderException extends RuntimeException {
    public OAuth1HeaderBuilderException(String message) {
        super(message);
    }

    public OAuth1HeaderBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
