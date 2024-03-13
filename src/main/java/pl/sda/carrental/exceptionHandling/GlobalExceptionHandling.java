package pl.sda.carrental.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(ObjectNotFoundInRepositoryException.class)
    public ResponseEntity<Object> handleObjectNotFoundInRepository(ObjectNotFoundInRepositoryException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReservationTimeCollisionException.class)
    public ResponseEntity<Object> handleReservationTimeCollisionException(ReservationTimeCollisionException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ObjectAlreadyAssignedToBranchException.class)
    public ResponseEntity<Object> handleClientAlreadyAssignedToBranch(ObjectAlreadyAssignedToBranchException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BranchAlreadyOpenInCityException.class)
    public ResponseEntity<Object> handleBranchAlreadyOpenInCity(BranchAlreadyOpenInCityException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RentAlreadyExistsForReservationException.class)
    public ResponseEntity<Object> handleRentAlreadyExistsForReservation(RentAlreadyExistsForReservationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ReturnAlreadyExistsForReservationException.class)
    public ResponseEntity<Object> handleReturnAlreadyExistsForReservation(ReturnAlreadyExistsForReservationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<Object> handleCarRentalAlreadyExistsException(ObjectAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentForStatusException.class)
    public ResponseEntity<Object> handleIllegalArgumentForStatusException(IllegalArgumentForStatusException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String s = fieldError.getField() + " " + fieldError.getDefaultMessage();
            errors.add(s);
        }
        return new ResponseEntity<>(errors.toString(), HttpStatus.BAD_REQUEST);
    }
}
