package com.github.adeshmukh.ps4j;

/**
 * @author adeshmukh
 */
public class Ps4jException extends RuntimeException {

    private static final long serialVersionUID = -6395467049547277527L;

    public Ps4jException(Exception e) {
        super(e);
    }

}
