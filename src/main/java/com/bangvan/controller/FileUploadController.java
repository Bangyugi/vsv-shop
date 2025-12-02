package com.bangvan.controller;

import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "File Upload API")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Image", description = "Upload an image file and get the URL")
    public ResponseEntity<ApiResponse> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileStorageService.uploadFile(file);

        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "File uploaded successfully",
                fileUrl // Trả về String URL
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}