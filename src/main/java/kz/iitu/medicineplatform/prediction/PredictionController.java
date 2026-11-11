package kz.iitu.medicineplatform.prediction;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService service;

    public PredictionController(PredictionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a prediction record", description = "Saves either the exploratory human AChE IC50 ML baseline result or an explicitly marked demo result for unsupported activity types.")
    @ResponseStatus(HttpStatus.CREATED)
    public PredictionResponse predict(@Valid @RequestBody PredictionRequest request) {
        return service.predict(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prediction record", description = "Returns the saved input, provider version, demo flag, and limitations.")
    public PredictionResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
