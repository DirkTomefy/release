package mg.bovit.release.imports;

import mg.bovit.release.imports.parser.FileParser;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileParserTest {

    @Test
    public void testParseCsv() throws Exception {
        var res = new ClassPathResource("imports/pesee_sample.csv");
        var bytes = Files.readAllBytes(res.getFile().toPath());
        MockMultipartFile mf = new MockMultipartFile("file", "pesee_sample.csv", "text/csv", bytes);
        List<Map<String, String>> rows = FileParser.parseCsv(mf);
        assertEquals(2, rows.size());
        assertEquals("ABC123", rows.get(0).get("bovin_tag"));
        assertEquals("350.5", rows.get(0).get("poids_kg"));
    }
}
