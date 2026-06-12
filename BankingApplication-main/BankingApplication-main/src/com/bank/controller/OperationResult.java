package com.bank.controller;

public class OperationResult {

    private final boolean success;
    private final String message;
    private final Object data;

    private OperationResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static OperationResult success(String message) {
        return new OperationResult(true, message, null);
    }

    public static OperationResult success(String message, Object data) {
        return new OperationResult(true, message, data);
    }

    public static OperationResult failure(String message) {
        return new OperationResult(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return (success ? "[OK] " : "[FAIL] ") + message;
    }
}
