package com.example.nftfabric.model;


import java.util.Map;

public class NftDTO {
    private String tokenId;
    private String owner;
    private String tokenUri;

    private Map<String, Object> metadata;


    public NftDTO() {}

    public NftDTO(String tokenId, String owner, String tokenUri, Map<String, Object> metadata) {
        this.tokenId = tokenId;
        this.owner = owner;
        this.tokenUri = tokenUri;
        this.metadata = metadata;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
