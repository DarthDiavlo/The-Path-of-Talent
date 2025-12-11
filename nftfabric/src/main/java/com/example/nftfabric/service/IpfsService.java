package com.example.nftfabric.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@Service
public class IpfsService {

    private final IPFS ipfs;

    public IpfsService() {
        // Подключение к локальному IPFS daemon
        this.ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
    }

    // Загружает файл и возвращает ipfs://<CID>
    public String uploadFile(File file) throws Exception {
        NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file);
        MerkleNode addResult = ipfs.add(fileWrapper).get(0);
        return "ipfs://" + addResult.hash.toString();
    }

    public JSONObject fetchJson(String ipfsUri) throws IOException {
        String hash = ipfsUri.replace("ipfs://", "");
        byte[] fileBytes = ipfs.cat(Multihash.fromBase58(hash));
        String jsonText = new String(fileBytes, StandardCharsets.UTF_8);
        return new JSONObject(jsonText);
    }
    public byte[] fetchFileAsBytes(String ipfsUri) throws IOException {
        String hash = ipfsUri.replace("ipfs://", "");
        return ipfs.cat(Multihash.fromBase58(hash));
    }

}
