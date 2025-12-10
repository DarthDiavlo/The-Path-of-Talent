package com.example.nftfabric.service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;

//@Service
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
}
