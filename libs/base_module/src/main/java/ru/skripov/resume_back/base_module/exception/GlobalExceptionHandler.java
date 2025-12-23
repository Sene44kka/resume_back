package ru.skripov.resume_back.base_module.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

//    @ExceptionHandler(FormException.class)
//    public ResponseEntity<FormErrorResponse> handleCustomFormException(FormException exception) {
//        log.error(exception);
//
//        FormErrorResponse errorResponse = new FormErrorResponse(
//                HttpStatus.BAD_REQUEST.value(),
//                exception.getCode(),
//                exception.getField(),
//                exception.getMessage()
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception exception) {
//        log.error(exception.getStackTrace());
//
//        ErrorResponse errorResponse = new ErrorResponse(
//            HttpStatus.SERVICE_UNAVAILABLE.value(),
//            "Bad Request",
//                exception.getMessage()
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
//    }
//
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
//        log.error(exception.getStackTrace());
//
//        ErrorResponse errorResponse = new ErrorResponse(
//            HttpStatus.UNAUTHORIZED.value(),
//            "Unauthorized",
//            "Authentication failed"
//        );
//        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
//    }
}

