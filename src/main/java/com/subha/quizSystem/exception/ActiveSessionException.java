package com.subha.quizSystem.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ActiveSessionException extends Throwable {
    public ActiveSessionException(String msg) {
        super(msg);
    }
}
