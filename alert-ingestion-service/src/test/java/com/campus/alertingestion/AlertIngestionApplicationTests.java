package com.campus.alertingestion;

import com.campus.alertingestion.dto.CreateAlertRequest;
import com.campus.alertingestion.model.Severity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AlertIngestionApplicationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validAlertRequest_shouldPassValidation() {
        CreateAlertRequest request = new CreateAlertRequest();
        request.setTitle("Suspicious activity near SAC");
        request.setSeverity(Severity.HIGH);
        request.setLat(40.9136);
        request.setLng(-73.1235);
        request.setCampusZone("Student Activities Center");

        Set<ConstraintViolation<CreateAlertRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void missingTitle_shouldFailValidation() {
        CreateAlertRequest request = new CreateAlertRequest();
        request.setSeverity(Severity.HIGH);
        request.setLat(40.9136);
        request.setLng(-73.1235);

        Set<ConstraintViolation<CreateAlertRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void missingSeverity_shouldFailValidation() {
        CreateAlertRequest request = new CreateAlertRequest();
        request.setTitle("Test alert");
        request.setLat(40.9136);
        request.setLng(-73.1235);

        Set<ConstraintViolation<CreateAlertRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("severity")));
    }

    @Test
    void missingCoordinates_shouldFailValidation() {
        CreateAlertRequest request = new CreateAlertRequest();
        request.setTitle("Test alert");
        request.setSeverity(Severity.LOW);

        Set<ConstraintViolation<CreateAlertRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size()); // lat and lng both missing
    }
}
