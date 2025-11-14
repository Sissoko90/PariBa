package com.example.pariba.exceptions;

public class AlreadyExistsException extends RuntimeException {
    
    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public AlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s existe déjà avec %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public AlreadyExistsException(String message) {
        super(message);
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName() { return fieldName; }
    public Object getFieldValue() { return fieldValue; }
}
