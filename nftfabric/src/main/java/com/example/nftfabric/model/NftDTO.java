package com.example.nftfabric.model;

public class NftDTO {
    private String tokenId;
    private String owner;
    private String uri;

    public NftDTO() {}

    public NftDTO(String tokenId, String owner, String uri) {
        this.tokenId = tokenId;
        this.owner = owner;
        this.uri = uri;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
