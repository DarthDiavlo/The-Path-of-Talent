package com.example.nftfabric.service;

import com.example.nftfabric.model.ComposeRequest;
import com.example.nftfabric.model.ImageComposeRequest;
import com.example.nftfabric.model.NftDTO;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class FabricService {

    private final Contract contract;
    private final ImageLowService imageService;

    public FabricService(Gateway gateway, ImageLowService imageService) {
        Network network = gateway.getNetwork("mychannel");
        this.contract = network.getContract("token_erc721");
        this.imageService = imageService;
    }

    // Получить информацию о NFT
    public NftDTO getNFT(String tokenId) throws Exception {
        String owner = new String(contract.evaluateTransaction("OwnerOf", tokenId));
        String tokenURI = new String(contract.evaluateTransaction("TokenURI", tokenId));
        return new NftDTO(tokenId, owner, tokenURI);
    }

    public String name() throws Exception {
        return new String(contract.evaluateTransaction("Name"));
    }

    public String symbol() throws Exception {
        return new String(contract.evaluateTransaction("Symbol"));
    }

    public String totalSupply() throws Exception {
        return new String(contract.evaluateTransaction("TotalSupply"));
    }

    // --- Mint NFT без IPFS ---
    public String mintNFTWithIPFS(ComposeRequest request) throws Exception {

        // 1️⃣ Генерация изображения
        BufferedImage composedImage = imageService.composeImages(request.getLayers(), request.getText());

        // 2️⃣ Конвертация изображения в Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(composedImage, "png", baos);
        String imageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        // 3️⃣ Создание JSON метаданных
        JSONObject metadata = new JSONObject();
        String tokenId = UUID.randomUUID().toString();

        metadata.put("name", request.getText());
        metadata.put("description", "NFT сгенерированный по уровням достижений");
        metadata.put("image", "data:image/png;base64," + imageBase64);

        // Добавляем атрибуты из слоёв
        JSONArray attributes = new JSONArray();
        for (ImageComposeRequest layer : request.getLayers()) {
            JSONObject attr = new JSONObject();
            attr.put("subcategory", layer.getCategory());
            attr.put("level", layer.getLevel());
            attributes.put(attr);
        }
        metadata.put("attributes", attributes);

        // 4️⃣ Минт NFT в Fabric с JSON как tokenURI
        String result = new String(
                contract.submitTransaction("MintWithTokenURI", tokenId, metadata.toString())
        );

        return result;
    }
}
