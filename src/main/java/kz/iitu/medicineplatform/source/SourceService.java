package kz.iitu.medicineplatform.source;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import kz.iitu.medicineplatform.api.PageResponse;
import org.springframework.data.domain.Pageable;
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

    public PageResponse<SourceResponse> list(Pageable pageable) {
        return PageResponse.from(repository.findAll(pageable).map(SourceResponse::from));
    }

    @Transactional
    public SourceResponse create(CreateSourceRequest request) {
        if ((request.doi() == null || request.doi().isBlank())
                && (request.url() == null || request.url().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide a DOI or URL for the source");
        }
        DataSource source = new DataSource(request.title().trim(), normalize(request.authors()), request.publicationYear(),
                normalize(request.doi()), normalize(request.url()), request.sourceType(),
                OffsetDateTime.now(ZoneOffset.UTC), normalize(request.licenseNote()));
        return SourceResponse.from(repository.save(source));
    }

    private String normalize(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
