package com.hutchison.rnd.dogs_api.exception;

import com.hutchison.rnd.dogs_api.constant.DatabaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericExceptions(Exception ex){
        log.error("Exception occurred!!", ex);

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


    @ExceptionHandler(BreedNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBreedNotFoundException(BreedNotFoundException ex){
        log.warn("Breed not found!!", ex);
        return buildError(HttpStatus.NOT_FOUND, "The requested breed does not exist, please refresh and try again");
    }

    @ExceptionHandler(BreedVersionConflictException.class)
    public ResponseEntity<Map<String, Object>> handleBreedVersionConflictException(BreedVersionConflictException ex){
        log.warn("Breed was modified by another user, please refresh and try again!!", ex);
        return buildError(HttpStatus.CONFLICT, "Breed was modified by another user, please refresh and try again");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex){
        log.warn("Breed was modified by another user, please refresh and try again!!", ex);
        return buildError(HttpStatus.CONFLICT, "The Data was modified by another user, please refresh and try again!!");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        log.warn("Data integrity violation", ex);

        String constraintName = getConstraintName(ex);


        if (constraintName != null && constraintName.toLowerCase().contains(DatabaseConstants.UNIQUE_BREED)) {
            return buildError(
                    HttpStatus.CONFLICT,
                    "Breed already exists"
            );
        }

        if (constraintName != null && constraintName.toLowerCase().contains(DatabaseConstants.UNIQUE_SUB_BREED)) {
            return buildError(
                    HttpStatus.CONFLICT,
                    "Sub-breed already exists"
            );
        }

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong!!"
        );
    }

    private String getConstraintName(Throwable ex) {
        while (ex != null) {
            if (ex instanceof ConstraintViolationException cve) {
                return cve.getConstraintName();
            }
            ex = ex.getCause();
        }
        return null;
    }


    @ExceptionHandler(SubBreedNotFoundUnderBreedException.class)
    public ResponseEntity<Map<String, Object>> handleSubBreedNotFoundUnderBreedException(SubBreedNotFoundUnderBreedException ex){
        log.warn("Sub-breed not found under breed!!", ex);
        return buildError(HttpStatus.NOT_FOUND, "Sub-breed not found under the specified breed");
    }

    @ExceptionHandler(SubBreedNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSubBreedNotFoundException(SubBreedNotFoundException ex){
        log.warn("Sub-breed not found!!", ex);
        return buildError(HttpStatus.NOT_FOUND, "The requested sub-breed does not exist, please refresh and try again");
    }

    @ExceptionHandler(SubBreedVersionConflictException.class)
    public ResponseEntity<Map<String, Object>> handleSubBreedVersionConflictException(SubBreedVersionConflictException ex){
        log.warn("Sub-breed was modified by another user, please refresh and try again!!", ex);
        return buildError(HttpStatus.CONFLICT, "Sub-breed modified by another user, please refresh and try again");
    }

    @ExceptionHandler(DuplicateSubBreedsFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateSubBreedsFoundException(DuplicateSubBreedsFoundException ex){
        log.warn("Duplicate Sub-breeds found!!", ex);
        return buildError(HttpStatus.BAD_REQUEST, "Duplicate sub-breeds found");
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
        errorMap.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(errorMap, status);
    }
}
