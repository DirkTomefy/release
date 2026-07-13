package mg.bovit.release.service;

import mg.bovit.release.imports.parser.FileParser;
import mg.bovit.release.model.ImportJob;
import mg.bovit.release.repository.ImportJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ImportServiceImpl implements ImportService {

    private final ImportJobRepository jobRepository;

    public ImportServiceImpl(ImportJobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    @Transactional
    public List<Map<String, String>> preview(MultipartFile file, String filename) throws Exception {
        if (file == null || file.isEmpty()) return Collections.emptyList();
        String lower = filename.toLowerCase();
        List<Map<String, String>> rows;
        if (lower.endsWith(".xls") || lower.endsWith(".xlsx")) {
            rows = FileParser.parseExcel(file);
        } else {
            rows = FileParser.parseCsv(file);
        }
        // create a job record with preview status
        ImportJob job = new ImportJob();
        job.setType("preview");
        job.setStatus("PREVIEW");
        job.setImportedCount(0);
        job.setFailedCount(0);
        job.setSkippedCount(0);
        jobRepository.save(job);
        return rows;
    }
}
