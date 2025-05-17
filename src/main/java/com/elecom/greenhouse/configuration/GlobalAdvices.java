package com.elecom.greenhouse.configuration;

import com.elecom.greenhouse.model.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalAdvices {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error(
                "Internal server error. Request URI: {}, HTTP Method: {}, Query String: {}, Remote Address: {}, Error Message: {}",
                requestUri,
                request.getMethod(),
                request.getQueryString(),
                request.getRemoteAddr(),
                ex.getMessage());

        if (requestUri != null && requestUri.startsWith("/actuator/prometheus")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .contentType(MediaType.TEXT_PLAIN)
                                 .body("Internal server error occurred.");
        }

        ErrorResponse errorResponse = new ErrorResponse("Unexpected error occurred. Please try again");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(errorResponse);
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Object handleResourceAccessException(RuntimeException exception,
                                                HttpServletRequest request) {

        log.error(
                "Runtime error. Request URI: {}, HTTP Method: {}, Query String: {}, Remote Address: {}, Error Message: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getQueryString(),
                request.getRemoteAddr(),
                exception.getMessage());

        return new ErrorResponse("Ошибка: " + exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object handleRuntimeException(RuntimeException exception, HttpServletRequest request) {

        log.error(
                "Runtime error. Request URI: {}, HTTP Method: {}, Query String: {}, Remote Address: {}, Error Message: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getQueryString(),
                request.getRemoteAddr(),
                exception.getMessage());

        return new ErrorResponse("Ошибка: " + exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Object handleIllegalArgumentException(IllegalArgumentException exception,
                                                 HttpServletRequest request) {
        log.error(
                "Illegal argument error. Request URI: {}, HTTP Method: {}, Query String: {}, Remote Address: {}, Error Message: {}",
                request.getRequestURI(),
                request.getMethod(),
                request.getQueryString(),
                request.getRemoteAddr());
        return new ErrorResponse("Illegal argument error. Request URI: " + request.getRequestURI());
    }

}
