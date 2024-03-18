package com.ritzjucy.technicaldrawingsbackend.exception;

import com.ritzjucy.technicaldrawingsbackend.model.response.ErrorResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class WebExceptionHandler extends ResponseEntityExceptionHandler
{

    @ExceptionHandler(value = { ValidationException.class})
    protected ResponseEntity<Object> handleValidationException(RuntimeException ex, WebRequest request)
    {
        ValidationException exception = (ValidationException) ex;

        return ResponseEntity.status(400)
                .body(ErrorResponseModel.builder()
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(value = { AuthException.class})
    protected ResponseEntity<Object> handleAuthException(RuntimeException ex, WebRequest request)
    {
        AuthException exception = (AuthException) ex;

        return ResponseEntity.status(401)
                .body(ErrorResponseModel.builder()
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(value = { NotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(RuntimeException ex, WebRequest request)
    {
        NotFoundException exception = (NotFoundException) ex;

        return ResponseEntity.status(404)
                .body(ErrorResponseModel.builder()
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(value = { Exception.class})
    protected ResponseEntity<Object> handleInternalException(RuntimeException ex, WebRequest request)
    {
        logger.warn("internal error", ex);

        return ResponseEntity.status(500)
                .body(ErrorResponseModel.builder()
                        .message("Internal server error")
                        .build());
    }

}
