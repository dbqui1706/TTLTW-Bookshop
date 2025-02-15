package com.example.bookshopwebapplication.service;

import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyService {
    private static final String PROVIDER = "BCFIPS";
    private static final String RSA_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    @Getter
    @Setter
    private PublicKey publicKey;
    @Getter
    @Setter
    private PrivateKey privateKey;
    private KeyPairGenerator keyPairGenerator;
    private Signature signature;

    // Khởi tạo các tham số và tạo cặp khóa RSA
    public void initVariable() {
        Security.addProvider(new BouncyCastleFipsProvider());
        try {
            this.keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM, PROVIDER);
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            this.keyPairGenerator.initialize(KEY_SIZE, secureRandom);
            this.signature = Signature.getInstance("SHA256withRSA", PROVIDER);
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String parseKeyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public void generateKeyPair() {
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    // Ký số một chuỗi văn bản bằng khóa riêng
    public String sign(String message) {
        try {
            byte[] data = message.getBytes();
            signature.initSign(privateKey);
            signature.update(data);
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (InvalidKeyException | SignatureException e) {
            System.err.println("Error during signing: " + e);
            return "Error during signing: " + e.getMessage();
        }
    }

    // Xác minh chữ ký của một chuỗi văn bản bằng khóa công khai
    public boolean verify(String message, byte[] signatureData) {
        try {
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureData);
        } catch (InvalidKeyException | SignatureException e) {
            System.err.println("Error during verification: " + e);
            return false;
        }
    }

    public Key parseKey(String keyString) {
        // Loại bỏ khoảng trắng
        // Xác định loại khóa dựa trên tiêu đề của PEM và gọi các hàm thích hợp
        try {
            if (keyString.contains("BEGIN PRIVATE KEY")) {
                // PKCS#8 Private Key
                return pemToPrivateKey(keyString);
            } else if (keyString.contains("BEGIN PUBLIC KEY")) {
                // PKCS#8 Public Key
                return pemToPublicKey(keyString);
            } else {
                throw new IllegalArgumentException("Unsupported key format");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing key: " + e.getMessage(), e);
        }
    }

    private String encodeToPEM(String type, byte[] keyBytes) throws IOException {
        StringWriter stringWriter = new StringWriter();
        try (PemWriter pemWriter = new PemWriter(stringWriter)) {
            pemWriter.writeObject(new PemObject(type, keyBytes));
        }
        return stringWriter.toString();
    }

    // Chuyển đổi khóa công khai thành định dạng PEM
    public String publicKeyToPEM(PublicKey publicKey) {
        try {
            String type = "PUBLIC KEY";
            return encodeToPEM(type, publicKey.getEncoded());
        } catch (IOException e) {
            throw new RuntimeException("Error converting public key to PEM format", e);
        }
    }

    // Chuyển đổi khóa riêng thành định dạng PEM
    public String privateKeyToPEM(PrivateKey privateKey) {
        try {
            String type = "PRIVATE KEY";
            return encodeToPEM(type, privateKey.getEncoded());
        } catch (IOException e) {
            throw new RuntimeException("Error converting private key to PEM format", e);
        }
    }

    // Chuyển đổi chuỗi PEM thành khóa riêng dạng PKCS#8
    public PrivateKey pemToPrivateKey(String pemPrivateKey) {
        try {
            byte[] keyBytes = decodeFromPEM(pemPrivateKey,
                    "-----BEGIN PRIVATE KEY-----",
                    "-----END PRIVATE KEY-----");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM, PROVIDER);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error reading private key from PEM", e);
        }
    }

    // Chuyển đổi chuỗi PEM thành khóa công khai dạng PKCS#8
    public PublicKey pemToPublicKey(String pemPublicKey) {
        try {
            byte[] keyBytes = decodeFromPEM(pemPublicKey,
                    "-----BEGIN PUBLIC KEY-----",
                    "-----END PUBLIC KEY-----"
            );
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM, PROVIDER);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error reading public key from PEM", e);
        }
    }

    private byte[] decodeFromPEM(String pemStr, String header, String footer) {
        String cleanKey = pemStr.replace(header, "").replace(footer, "").replaceAll("\\s", "");
        return Base64.getDecoder().decode(cleanKey);
    }
}
