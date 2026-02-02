package com.landisgyr.gfc.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Base64;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FlexibilityUploadService {
  private static final Logger logger = LoggerFactory.getLogger(FlexibilityUploadService.class);
  private static final String COLLECTION_NAME = "flexibility_uploads_rs";

  private final MongoCollection<Document> collection;

  @Inject
  public FlexibilityUploadService(MongoClient mongoClient, @Named("dbName") String databaseName) {
    this.collection = mongoClient.getDatabase(databaseName).getCollection(COLLECTION_NAME);
    logger.info("FlexibilityUploadService initialized");
  }

  public void storeUpload(String uploadId, String filename, byte[] content, String orgCode) {
    Document doc =
        new Document()
            .append("uploadId", uploadId)
            .append("filename", filename)
            .append("content", Base64.getEncoder().encodeToString(content))
            .append("orgCode", orgCode)
            .append("status", "pending")
            .append("createdAt", System.currentTimeMillis());

    collection.insertOne(doc);
    logger.info("Stored upload: {}", uploadId);
  }

  public byte[] retrieveUpload(String uploadId) {
    Document doc = collection.find(Filters.eq("uploadId", uploadId)).first();
    if (doc == null) {
      throw new RuntimeException("Upload not found: " + uploadId);
    }
    String base64Content = doc.getString("content");
    return Base64.getDecoder().decode(base64Content);
  }

  public String getOrgCode(String uploadId) {
    Document doc = collection.find(Filters.eq("uploadId", uploadId)).first();
    if (doc == null) {
      throw new RuntimeException("Upload not found: " + uploadId);
    }
    return doc.getString("orgCode");
  }

  public void updateStatus(String uploadId, String status) {
    collection.updateOne(
        Filters.eq("uploadId", uploadId),
        Updates.combine(
            Updates.set("status", status), Updates.set("updatedAt", System.currentTimeMillis())));
    logger.info("Updated upload {} status to: {}", uploadId, status);
  }
}
