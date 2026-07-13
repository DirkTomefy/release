package mg.bovit.release.controller;

import mg.bovit.release.service.ImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/preview", consumes = {"multipart/form-data"})
    public ResponseEntity<?> preview(@RequestParam("file") MultipartFile file) {
        try {
            List<Map<String, String>> rows = importService.preview(file, file.getOriginalFilename());
            Map<String, Object> resp = new HashMap<>();
            resp.put("rows", rows.size());
            resp.put("preview", rows.stream().limit(10).toList());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
