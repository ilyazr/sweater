package ru.zakharov.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private Map<String, Object> createBodyOfResponse(RuntimeException ex,
                                                     WebRequest req,
                                                     HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Calendar.getInstance());
        body.put("status", status.value());
        body.put("error", ex.getMessage());
        body.put("path", ((ServletWebRequest) req).getRequest().getRequestURI());
        return body;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Calendar.getInstance());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConflict(RuntimeException ex,
                                                    WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Calendar.getInstance());
        body.put("status", status.value());
        body.put("error", ex.getMessage().split(": ")[1]);

        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    @ExceptionHandler({WallMessageException.class, FileStorageException.class})
    protected ResponseEntity<?> handleBadRequest(RuntimeException ex,
                                                           WebRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> body = createBodyOfResponse(ex, req, status);
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, req);
    }

    @ExceptionHandler({UserNotFoundException.class, FriendshipException.class, ChatException.class})
    protected ResponseEntity<?> handleNotFound(RuntimeException ex,
                                                           WebRequest req) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Map<String, Object> body = createBodyOfResponse(ex, req, status);
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, req);
    }

    @ExceptionHandler({LikeException.class})
    protected ResponseEntity<?> handleConflictSecond(RuntimeException ex,
                                                           WebRequest req) {
        HttpStatus status = HttpStatus.CONFLICT;
        Map<String, Object> body = createBodyOfResponse(ex, req, status);
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, req);
    }

    @ExceptionHandler({IllegalStateException.class})
    protected ResponseEntity<?> handleForbidden(RuntimeException ex,
                                                           WebRequest req) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        Map<String, Object> body = createBodyOfResponse(ex, req, status);
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, req);
    }

}
