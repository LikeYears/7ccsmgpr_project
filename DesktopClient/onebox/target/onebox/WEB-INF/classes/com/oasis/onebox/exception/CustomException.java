package com.oasis.onebox.exception;

public class CustomException extends Exception {
    private static final long serialVersionUID = 6465464L;
    private int httpStatus;

    public CustomException(int httpStatus, String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
