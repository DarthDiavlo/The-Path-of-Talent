package com.example.nftfabric.controller;

import com.example.nftfabric.model.ImageComposeRequest;
import com.example.nftfabric.model.ImageData;
import com.example.nftfabric.repository.ImageDataRepository;
import com.example.nftfabric.service.ImageLowService;
import com.example.nftfabric.service.ImageService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

//@RestController
//@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;
    private  final ImageDataRepository repository;


    public ImageController(ImageService imageService, ImageDataRepository repository) {
        this.imageService = imageService;
        this.repository = repository;
    }

    @PostMapping(value = "/compose", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> compose(@RequestBody List<ImageComposeRequest> requests) throws Exception {
        BufferedImage result = imageService.composeImages(requests);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "jpg", baos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=result.jpg")
                .contentType(MediaType.IMAGE_JPEG)
                .body(baos.toByteArray());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestParam String category,
            @RequestParam String subcategory,
            @RequestParam int level,
            @RequestParam int m,
            @RequestParam int u,
            @RequestParam int s,
            @RequestParam int i,
            @RequestParam int c,
            @RequestParam("file") MultipartFile file) throws IOException {

        ImageData entity = new ImageData();
        entity.setCategory(category);
        entity.setSubcategory(subcategory);
        entity.setLevel(level);
        entity.setM(m);
        entity.setU(u);
        entity.setS(s);
        entity.setI(i);
        entity.setC(c);
        entity.setImage(file.getBytes());

        repository.save(entity);
        return ResponseEntity.ok("Image saved successfully!");
    }


}