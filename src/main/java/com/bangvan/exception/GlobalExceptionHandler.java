package com.bangvan.exception;


import com.bangvan.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle specific exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest)
    {
        ApiResponse apiResponse = ApiResponse.error(404, exception.getMessage());
        webRequest.getDescription(false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException exception, WebRequest webRequest){
        ApiResponse apiResponse = ApiResponse.error(exception.getErrorCode().getCode(), exception.getMessage());
        webRequest.getDescription(false);
        return new ResponseEntity<>(apiResponse,exception.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception exception) {
        ApiResponse apiResponse= ApiResponse.error(500, exception.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse apiResponse = ApiResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                "Dữ liệu đầu vào không hợp lệ"
        );
        apiResponse.setData(errors);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }



}



