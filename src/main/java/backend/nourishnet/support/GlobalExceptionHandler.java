package backend.nourishnet.support;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> fields = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(f -> new ApiError.FieldViolation(f.getField(), messageFor(f)))
                .toList();
        return ResponseEntity.badRequest().body(new ApiError(
                req.getRequestURI(), 400, "Bad Request", "Validation failed",
                OffsetDateTime.now(), fields));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(
                req.getRequestURI(), 409, "Conflict", rootMessage(ex),
                OffsetDateTime.now(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(
                req.getRequestURI(), 403, "Forbidden", "Insufficient permissions",
                OffsetDateTime.now(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(new ApiError(
                req.getRequestURI(), 400, "Bad Request", ex.getMessage(),
                OffsetDateTime.now(), null));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleGeneric(RuntimeException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
                req.getRequestURI(), 500, "Internal Server Error", rootMessage(ex),
                OffsetDateTime.now(), null));
    }

    private String messageFor(FieldError f) {
        return f.getDefaultMessage() != null ? f.getDefaultMessage() : "invalid";
    }
    private String rootMessage(Throwable t) {
        Throwable x = t;
        while (x.getCause() != null) x = x.getCause();
        return x.getMessage();
    }
}
