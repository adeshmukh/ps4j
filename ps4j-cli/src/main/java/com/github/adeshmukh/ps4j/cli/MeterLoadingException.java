package com.github.adeshmukh.ps4j.cli;

public class MeterLoadingException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Exception cause;

    MeterLoadingException(Exception e) {
        cause = e;
    }

    @Override
    public Exception getCause() {
        return cause;
    }
}
