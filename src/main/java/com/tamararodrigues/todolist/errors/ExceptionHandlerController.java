package com.tamararodrigues.todolist.errors;

import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //interceptador global
public class ExceptionHandlerController {

    //smp que ocorrer o erro HttpMessageNotReadableException, devolva o erro 400 com a msg explicativa no caso a do title
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity <String> handleHttpMenssageNotReadableException(HttpMessageNotReadableException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMostSpecificCause().getMessage());
    }
}
