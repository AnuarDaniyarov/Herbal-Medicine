package kz.iitu.medicineplatform.prediction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PredictionProviderConfiguration {
    @Bean
    PredictionProvider predictionProvider(
            @Value("${app.prediction.model-path:./data/processed/ache-baseline-model.json}") String modelPath) {
        return new RoutedPredictionProvider(new DemoPredictionProvider(),
                new AChEBaselinePredictionProvider(new ObjectMapper(), modelPath));
    }
}
