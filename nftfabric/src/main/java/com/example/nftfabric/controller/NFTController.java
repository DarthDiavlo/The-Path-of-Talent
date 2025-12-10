package com.example.nftfabric.controller;

import com.example.nftfabric.model.*;
import com.example.nftfabric.service.FabricService;
import com.example.nftfabric.service.ImageLowService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/nft")
public class NFTController {

    private final FabricService fabricService;
    private final ImageLowService imageService;

    public NFTController(FabricService fabricService,ImageLowService imageService) {

        this.fabricService = fabricService;
        this.imageService = imageService;
    }

    // --- GET методы ---
    @GetMapping("/{tokenId}")
    public NftDTO getNFT(@PathVariable String tokenId) throws Exception {
        return fabricService.getNFT(tokenId);
    }


    @GetMapping("/name")
    public String getName() throws Exception {
        return fabricService.name();
    }

    @GetMapping("/symbol")
    public String getSymbol() throws Exception {
        return fabricService.symbol();
    }

    @GetMapping("/total-supply")
    public String getTotalSupply() throws Exception {
        return fabricService.totalSupply();
    }


    // --- Mint с локальным IPFS ---
    @PostMapping("/mint-ipfs")
    public ResponseEntity<String> composeAndMintNFT(
            @RequestBody ComposeRequest request
    ) throws Exception {

        String result = fabricService.mintNFTWithIPFS(request);
        return ResponseEntity.ok("NFT создан! Fabric result: " + result);
    }


    @PostMapping(value = "/compose-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> composeImage(@RequestBody ComposeRequest request) throws Exception {
        BufferedImage result = imageService.composeImages(request.getLayers(), request.getText());

        // Конвертация изображения в байты для ответа
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "png", baos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=result.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(baos.toByteArray());
    }
}
