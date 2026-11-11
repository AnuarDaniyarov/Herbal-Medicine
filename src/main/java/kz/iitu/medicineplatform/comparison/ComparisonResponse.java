package kz.iitu.medicineplatform.comparison;

import java.util.List;
import kz.iitu.medicineplatform.activity.ActivityMeasurementResponse;
import kz.iitu.medicineplatform.compound.CompoundSummaryResponse;

public record ComparisonResponse(String activityCode, String notice, List<CandidateComparison> candidates) {
    public record CandidateComparison(CompoundSummaryResponse compound,
                                      List<ActivityMeasurementResponse> measurements) { }
}
