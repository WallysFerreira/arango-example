package org.example.model.exceptions;

public class EnvironmentVariableNotSetException extends RuntimeException {
    public EnvironmentVariableNotSetException(String variableName) {
      super("Variable " + variableName + " is not set");
    }
}
