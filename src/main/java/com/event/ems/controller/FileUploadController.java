package com.event.ems.controller;

import com.event.ems.factory.FileStorageFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final FileStorageFactory factory;

    public FileUploadController(FileStorageFactory factory) {
        this.factory = factory;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String type = resolveType(file);
            String path = factory.getService(type).store(file);

            return ResponseEntity.ok(path);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    private String resolveType(MultipartFile file) {
        String contentType = file.getContentType();

        if ("application/pdf".equals(contentType)) {
            return "PDF";
        }

        throw new IllegalArgumentException("Unsupported file type");
    }
}