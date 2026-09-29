package com.wuji.service.utils.mongo;


import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;

import java.util.HashMap;
import java.util.Map;

public class EncryptionHelper {
    private static final String LOCAL_MASTER_KEY =
            "MongoDBLocalKey12345678901234567MongoDBLocalKey12345678901234567MongoDBLocalKey12345678901234567";
    // 32字节本地主密钥

    public static MongoClient createEncryptedClient(String connectionString, String keyVaultNamespace) {
        // 创建本地密钥提供程序
        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        Map<String, Object> localMasterKey = new HashMap<>();
        localMasterKey.put("key", LOCAL_MASTER_KEY.getBytes());
        kmsProviders.put("local", localMasterKey);

        // 自动加密设置
        // AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
        //         .keyVaultNamespace(keyVaultNamespace)
        //         .kmsProviders(kmsProviders)
        //         .build();

        MongoClientSettings clientSettings =
                MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString))
                        .autoEncryptionSettings(AutoEncryptionSettings.builder().keyVaultNamespace(keyVaultNamespace)
                                .kmsProviders(kmsProviders).bypassAutoEncryption(true).build()).build();

        return MongoClients.create(clientSettings);
    }

    public static BsonBinary createDataKey(ClientEncryption clientEncryption) {
        return clientEncryption.createDataKey("local", new DataKeyOptions());
    }

    public static ClientEncryption getClientEncryption(String connectionString, String keyVaultNamespace) {
        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        Map<String, Object> localMasterKey = new HashMap<>();
        localMasterKey.put("key", LOCAL_MASTER_KEY.getBytes());
        kmsProviders.put("local", localMasterKey);

        ClientEncryptionSettings encryptionSettings = ClientEncryptionSettings.builder().keyVaultMongoClientSettings(
                        MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString)).build())
                .keyVaultNamespace(keyVaultNamespace).kmsProviders(kmsProviders).build();

        return ClientEncryptions.create(encryptionSettings);
    }
}

