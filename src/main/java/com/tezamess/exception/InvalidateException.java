
package com.tezamess.exception;

public class InvalidateException extends RuntimeException{

    String message = null;
    
    public InvalidateException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
}
