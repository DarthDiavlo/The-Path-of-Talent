package com.example.nftfabric.service;

import com.example.nftfabric.model.ComposeRequest;
import com.example.nftfabric.model.ImageComposeRequest;
import com.example.nftfabric.model.NftDTO;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.UUID;
import java.util.Map;

@Service
public class FabricService {

    private static final Logger log = LoggerFactory.getLogger(FabricService.class);

    private final Contract contract;
    private final IpfsService ipfsService;
    private final ImageLowService imageService;

    public FabricService(Gateway gateway, IpfsService ipfsService, ImageLowService imageService) {
        Network network = gateway.getNetwork("mychannel");
        this.contract = network.getContract("token_erc721");
        this.ipfsService = ipfsService;
        this.imageService = imageService;
    }

    public NftDTO getNFT(String tokenId) throws Exception {
        String owner = new String(contract.evaluateTransaction("OwnerOf", tokenId));
        String tokenURI = new String(contract.evaluateTransaction("TokenURI", tokenId));

        // Получаем метаданные из IPFS
        JSONObject metadataJson = ipfsService.fetchJson(tokenURI);
        Map<String, Object> metadata = metadataJson.toMap(); // <-- преобразуем в Map

        return new NftDTO(tokenId, owner, tokenURI, metadata);
    }


    public byte[] getNFTImage(String tokenId) throws Exception {
        // Получаем tokenURI
        String tokenURI = new String(contract.evaluateTransaction("TokenURI", tokenId));

        // Получаем JSON метаданных из IPFS
        JSONObject metadataJson = ipfsService.fetchJson(tokenURI);

        // Получаем ссылку на изображение
        String imageUri = metadataJson.getString("image");

        // Получаем байты изображения из IPFS
        return ipfsService.fetchFileAsBytes(imageUri);
    }

    // Имя коллекции
    public String name() throws Exception {
        return new String(contract.evaluateTransaction("Name"));
    }

    // Символ коллекции
    public String symbol() throws Exception {
        return new String(contract.evaluateTransaction("Symbol"));
    }

    // Общее количество токенов
    public String totalSupply() throws Exception {
        return new String(contract.evaluateTransaction("TotalSupply"));
    }


    // --- Mint NFT с генерацией изображения и загрузкой в IPFS ---
    public String mintNFTWithIPFS(ComposeRequest request) throws Exception {

        log.info("=== MINT NFT STARTED ===");
        log.info("Request text: {}", request.getText());
        log.info("Layers count: {}", request.getLayers().size());

        // 1️⃣ Генерация изображения
        log.info("Generating composed image...");
        BufferedImage composedImage = imageService.composeImages(request.getLayers(), request.getText());
        log.info("Image generated: {}x{}", composedImage.getWidth(), composedImage.getHeight());

        // 2️⃣ Сохраняем изображение во временный файл
        File tempImageFile = File.createTempFile("nft_image_", ".png");
        ImageIO.write(composedImage, "png", tempImageFile);
        log.info("Image saved to: {}", tempImageFile.getAbsolutePath());

        // 3️⃣ Загружаем изображение в IPFS
        log.info("Uploading image to IPFS...");
        String imageURI = ipfsService.uploadFile(tempImageFile);
        log.info("Image uploaded to IPFS: {}", imageURI);

        // 4️⃣ Создание JSON метаданных
        String tokenId = UUID.randomUUID().toString();
        log.info("Generated tokenId: {}", tokenId);

        JSONObject metadata = new JSONObject();
        metadata.put("name", request.getText());
        metadata.put("description", "NFT сгенерированный по уровням достижений");
        metadata.put("image", imageURI);

        JSONArray attributes = new JSONArray();
        for (ImageComposeRequest layer : request.getLayers()) {
            JSONObject attr = new JSONObject();
            attr.put("subcategory", layer.getCategory());
            attr.put("level", layer.getLevel());
            attributes.put(attr);
        }
        metadata.put("attributes", attributes);

        log.info("Metadata JSON created: {}", metadata.toString());

        // 5️⃣ Сохраняем метаданные во временный JSON файл
        File tempJsonFile = File.createTempFile("metadata_", ".json");
        try (FileWriter writer = new FileWriter(tempJsonFile)) {
            writer.write(metadata.toString());
        }
        log.info("Metadata saved to: {}", tempJsonFile.getAbsolutePath());

        // 6️⃣ Загружаем JSON в IPFS
        log.info("Uploading metadata JSON to IPFS...");
        String tokenURI = ipfsService.uploadFile(tempJsonFile);
        log.info("Metadata uploaded to IPFS: {}", tokenURI);

        // 7️⃣ Минтим NFT в Fabric
        log.info("Submitting MintWithTokenURI to Fabric...");
        String result = new String(contract.submitTransaction("MintWithTokenURI", tokenId, tokenURI));
        log.info("Fabric mint result: {}", result);

        // 8️⃣ Чистим временные файлы
        boolean imgDeleted = tempImageFile.delete();
        boolean jsonDeleted = tempJsonFile.delete();
        log.info("Temp files deleted? image={}, json={}", imgDeleted, jsonDeleted);

        log.info("=== MINT NFT FINISHED SUCCESSFULLY ===");

        return result;
    }
}
