package com.event.ems.factory;

import com.event.ems.service.file.FileStorageService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FileStorageFactory {

    private final Map<String, FileStorageService> services;

    public FileStorageFactory(Map<String, FileStorageService> services) {
        this.services = services;
    }

    public FileStorageService getService(String type) {
        FileStorageService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported file type");
        }
        return service;
    }
}
