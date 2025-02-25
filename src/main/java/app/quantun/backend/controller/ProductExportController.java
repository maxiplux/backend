package app.quantun.backend.controller;

import app.quantun.backend.service.ProductExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/export")
@RequiredArgsConstructor
@Slf4j
public class ProductExportController {

    private final ProductExportService productExportService;

    @PostMapping("/csv")
    public ResponseEntity<Long> exportToCsv(@RequestParam String filePath) {
        try {
            Long jobId = productExportService.exportProductsToCsv(filePath);
            return ResponseEntity.ok(jobId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/xlsx")
    public ResponseEntity<Long> exportToXlsx(@RequestParam String filePath) {
        try {
            Long jobId = productExportService.exportProductsToXlsx(filePath);
            return ResponseEntity.ok(jobId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/json")
    public ResponseEntity<Long> exportToJson(@RequestParam String filePath) {
        try {
            Long jobId = productExportService.exportProductsToJson(filePath);
            return ResponseEntity.ok(jobId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}