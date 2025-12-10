package com.example.nftfabric.model;

import java.util.List;

public class ComposeRequest {

    private String text; // это твоя одна фраза
    private List<ImageComposeRequest> layers; // список слоев

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<ImageComposeRequest> getLayers() { return layers; }
    public void setLayers(List<ImageComposeRequest> layers) { this.layers = layers; }
}
