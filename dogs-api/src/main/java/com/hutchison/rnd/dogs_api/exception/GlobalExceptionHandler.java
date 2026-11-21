package com.hutchison.rnd.dogs_api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericExceptions(Exception ex){
        log.error("Exception occured!!", ex);

        if (ex instanceof MethodArgumentTypeMismatchException ||
                ex instanceof HttpMessageNotReadableException) {

            return buildError(HttpStatus.BAD_REQUEST, "Invalid request");
        }

        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();

            return buildError(
                    HttpStatus.valueOf(status.value()),
                    errorResponse.getBody().getDetail()
            );
        }

        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!!");
    }

    @ExceptionHandler(BreedAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleBreedAlreadyExistsException(BreedAlreadyExistsException ex){
        log.warn("Breed already exists!!", ex);
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(BreedNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBreedNotFoundException(BreedNotFoundException ex){
        log.warn("Breed not found!!", ex);
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BreedVersionConflictException.class)
    public ResponseEntity<Map<String, Object>> handleBreedVersionConflictException(BreedVersionConflictException ex){
        log.warn("Breed was modified by another user, please refresh and try again!!", ex);
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex){
        log.warn("Breed was modified by another user, please refresh and try again!!", ex);
        return buildError(HttpStatus.CONFLICT, "The Data was modified by another user, please refresh and try again!!");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        log.warn("Something went wrong!!", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong!!");
    }

    @ExceptionHandler(SubBreedAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleSubBreedAlreadyExistsException(SubBreedAlreadyExistsException ex){
        log.warn("Sub-breed already exists!!", ex);
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SubBreedNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSubBreedNotFoundException(SubBreedNotFoundException ex){
        log.warn("Sub-breed not found!!", ex);
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SubBreedVersionConflictException.class)
    public ResponseEntity<Map<String, Object>> handleSubBreedVersionConflictException(SubBreedVersionConflictException ex){
        log.warn("Sub-breed was modified by another user, please refresh and try again!!", ex);
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DuplicateSubBreedsFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateSubBreedsFoundException(DuplicateSubBreedsFoundException ex){
        log.warn("Duplicate Sub-breeds found!!", ex);
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidImportFileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidImportFileException(InvalidImportFileException ex){
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        log.warn("Breed validation failed!!", ex);
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Breed Validation failed");
        return buildError(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message){
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", message);
        errorMap.put("status", status.value());
        errorMap.put("error", status.getReasonPhrase());
        errorMap.put("timestamp", LocalDateTime.now().toString());
        return new ResponseEntity<>(errorMap, status);
    }
}
