package com.event.ems.service.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service("PDF")
public class PdfStorageService implements FileStorageService {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Override
    public String store(MultipartFile file) throws IOException {

        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF files allowed");
        }

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(UPLOAD_DIR + fileName);

        file.transferTo(dest);

        return dest.getAbsolutePath();
    }
}
