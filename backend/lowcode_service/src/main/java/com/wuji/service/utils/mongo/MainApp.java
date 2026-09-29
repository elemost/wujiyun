package com.wuji.service.utils.mongo;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import org.bson.BsonBinary;
import org.bson.BsonString;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MainApp {
    private static final String CONNECTION_STRING =
            "mongodb://wuji_cloud_dev:123abc@124.221.111.26:27017/wuji_cloud_dev?replicaSet=cluster-replset&directConnection=true";
    private static final String KEY_VAULT_NAMESPACE = "wuji_cloud_dev.__keyVault";
    private static final String DB_NAME = "wuji_cloud_dev";
    private static final String COLLECTION_NAME = "encryptedCollection";

    public static void main(String[] args) {
        // 1. 创建数据加密密钥
        ClientEncryption clientEncryption =
                EncryptionHelper.getClientEncryption(CONNECTION_STRING, KEY_VAULT_NAMESPACE);
        BsonBinary dataKeyId = EncryptionHelper.createDataKey(clientEncryption);

        // 2. 创建支持加密的MongoClient
        MongoClient encryptedClient = EncryptionHelper.createEncryptedClient(CONNECTION_STRING, KEY_VAULT_NAMESPACE);
        MongoCollection<Document> collection = encryptedClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME);
        MongoTemplate mongoTemplate = new MongoTemplate(encryptedClient, DB_NAME);

        BsonBinary encryptedFoods = clientEncryption.encrypt(new BsonString("122333"),
                new EncryptOptions("AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic").keyId(dataKeyId));
        collection.insertOne(new Document("name", "张三").append("ssn",  encryptedFoods).append("keyId", dataKeyId));
        // 3. 插入加密文档
        // Document encryptedDoc = new Document().append("name", "张三").append("ssn", "123-45-6789")  // 这个字段会被自动加密
        //         .append("keyId", dataKeyId);
        // collection.insertOne(encryptedDoc);
        Document document = new Document("ssn", "122333");
        Document foundDoc = collection.find().first();
        // 4. 查询加密文档
        Query query = new Query();
        query.addCriteria(Criteria.where("ssn").is("122333"));
        List<JSONObject> jsonObjects = mongoTemplate.find(query, JSONObject.class, COLLECTION_NAME);
        System.out.println("查询结果: " + JSONObject.toJSONString(jsonObjects));
        assert foundDoc != null;
        System.out.println("查询结果1: " + foundDoc.toJson());

        clientEncryption.close();
        encryptedClient.close();
    }
}

