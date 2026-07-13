package mg.bovit.release.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ImportService {
    List<Map<String, String>> preview(MultipartFile file, String filename) throws Exception;
}
