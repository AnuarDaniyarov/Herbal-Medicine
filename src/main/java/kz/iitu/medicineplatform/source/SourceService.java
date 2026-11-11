package kz.iitu.medicineplatform.source;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class SourceService {

    private final DataSourceRepository repository;

    public SourceService(DataSourceRepository repository) {
        this.repository = repository;
    }

    public SourceResponse findById(Long id) {
        DataSource source = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Source with id " + id + " was not found"));
        return SourceResponse.from(source);
    }
}
