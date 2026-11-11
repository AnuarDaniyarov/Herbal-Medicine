package kz.iitu.medicineplatform.activity;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TargetService {
    private final TargetRepository targets;
    public TargetService(TargetRepository targets) { this.targets = targets; }
    public List<TargetResponse> findAll() {
        return targets.findAll(Sort.by(Sort.Direction.ASC, "normalizedIdentifier")).stream()
                .map(TargetResponse::from).toList();
    }
}
