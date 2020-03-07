package com.prototipo.aplicacion.Exception;


public class CustomerFieldValidationException extends Exception {

    private static final long serialVersionUID = -4995433707591853255L;

    private String fieldName;

    public CustomerFieldValidationException(String message, String fieldName) {
        super(message);
        this.fieldName= fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}