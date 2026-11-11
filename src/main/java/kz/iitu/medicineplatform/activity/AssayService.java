package kz.iitu.medicineplatform.activity;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AssayService {

    private final AssayRepository repository;

    public AssayService(AssayRepository repository) {
        this.repository = repository;
    }

    public List<AssayResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(AssayResponse::from)
                .toList();
    }
}
