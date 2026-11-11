package kz.iitu.medicineplatform.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Medicine Platform API",
        version = "1.0.0",
        description = "Research decision-support API for plant-derived compound discovery. Prediction responses are explicitly demo-only until a validated model is configured; they are not medical advice or evidence of efficacy or safety."))
public class OpenApiConfig {
}
