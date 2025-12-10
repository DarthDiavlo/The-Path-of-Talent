package com.example.nftfabric.config;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.hyperledger.fabric.client.identity.Signers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Configuration
public class FabricConfig {

    @Value("${fabric.msp-id}")
    private String mspId;

    @Value("${fabric.cert-path}")
    private String certificatePath;

    @Value("${fabric.key-path}")
    private String privateKeyPath;

    @Value("${fabric.peer-host}")
    private String peerHost;

    @Value("${fabric.peer-port}")
    private int peerPort;

    @Value("${fabric.tls-cert-path}")
    private String tlsCertPath;

    @Bean
    public Gateway gateway() throws Exception {

        X509Certificate certificate;
        PrivateKey privateKey;

        try (InputStream stream = new ClassPathResource(certificatePath).getInputStream();
             Reader reader = new InputStreamReader(stream)) {
            certificate = Identities.readX509Certificate(reader);
        }

        try (InputStream stream = new ClassPathResource(privateKeyPath).getInputStream();
             Reader reader = new InputStreamReader(stream)) {
            privateKey = Identities.readPrivateKey(reader);
        }

        Identity identity = new X509Identity(mspId, certificate);
        Signer signer = Signers.newPrivateKeySigner(privateKey);

        ManagedChannel channel = newGrpcConnection();

        return Gateway.newInstance()
                .identity(identity)
                .signer(signer)
                .connection(channel)
                .connect();
    }

    private ManagedChannel newGrpcConnection() throws Exception {
        X509Certificate tlsCert;

        try (InputStream stream = new ClassPathResource(tlsCertPath).getInputStream();
             Reader reader = new InputStreamReader(stream)) {
            tlsCert = Identities.readX509Certificate(reader);
        }

        return NettyChannelBuilder.forAddress(peerHost, peerPort)
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
                .overrideAuthority("peer0.org1.example.com")   // обязательно при IP
                .build();
    }
}
