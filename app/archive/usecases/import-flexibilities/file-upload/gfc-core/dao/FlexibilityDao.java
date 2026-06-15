package com.landisgyr.gfc.dao;

import com.landisgyr.gfc.domain.flexibility.Flexibility;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class FlexibilityRsDao {
  private static final Logger logger = LoggerFactory.getLogger(FlexibilityRsDao.class);
  private static final String COLLECTION_NAME = "flexibilities_rs";

  private final MongoCollection<Document> collection;

  /** Query result containing documents and total count */
  public static class QueryResult {
    private final List<Document> documents;
    private final long totalCount;

    public QueryResult(List<Document> documents, long totalCount) {
      this.documents = documents;
      this.totalCount = totalCount;
    }

    public List<Document> getDocuments() {
      return documents;
    }

    public long getTotalCount() {
      return totalCount;
    }
  }

  @Inject
  public FlexibilityRsDao(MongoClient mongoClient, @Named("dbName") String databaseName) {
    this.collection = mongoClient.getDatabase(databaseName).getCollection(COLLECTION_NAME);
    logger.info("FlexibilityRsDao initialized with collection: {}", COLLECTION_NAME);
  }

  /**
   * Find existing flexibility IDs in the database
   *
   * @param flexibilityIds List of flexibility IDs to check
   * @param orgCode Organization code
   * @return Set of existing flexibility IDs
   */
  public Set<String> findExistingFlexibilityIds(List<String> flexibilityIds, String orgCode) {
    logger.info(
        "Finding existing flexibility IDs. Count: {}, OrgCode: {}", flexibilityIds.size(), orgCode);

    Bson filter =
        Filters.and(Filters.in("flexibilityId", flexibilityIds), Filters.eq("orgCode", orgCode));

    Set<String> existingIds = new HashSet<>();

    try (MongoCursor<Document> cursor =
        collection.find(filter).projection(new Document("flexibilityId", 1)).iterator()) {
      while (cursor.hasNext()) {
        Document doc = cursor.next();
        existingIds.add(doc.getString("flexibilityId"));
      }
    }

    logger.info("Found {} existing flexibility IDs", existingIds.size());
    return existingIds;
  }

  /**
   * Find flexibilities with optional filtering and pagination
   *
   * @param flexibilityIdIn Optional list of flexibility IDs to filter by
   * @param flexibilityNameIn Optional list of flexibility names to filter by
   * @param flexibilityTypeIn Optional list of flexibility types to filter by
   * @param pageNumber Page number (1-based)
   * @param pageSize Number of items per page
   * @return QueryResult containing documents and total count
   */
  public QueryResult findFlexibilities(
      List<String> flexibilityIdIn,
      List<String> flexibilityNameIn,
      List<String> flexibilityTypeIn,
      int pageNumber,
      int pageSize) {

    logger.info("Querying flexibilities - Page: {}, Size: {}", pageNumber, pageSize);

    // Build filter
    List<Bson> filters = new ArrayList<>();

    if (flexibilityIdIn != null && !flexibilityIdIn.isEmpty()) {
      filters.add(Filters.in("flexibilityId", flexibilityIdIn));
    }

    if (flexibilityNameIn != null && !flexibilityNameIn.isEmpty()) {
      filters.add(Filters.in("flexibilityName", flexibilityNameIn));
    }

    if (flexibilityTypeIn != null && !flexibilityTypeIn.isEmpty()) {
      filters.add(Filters.in("flexibilityType", flexibilityTypeIn));
    }

    Bson filter = filters.isEmpty() ? new Document() : Filters.and(filters);

    // Count total matching documents
    long totalCount = collection.countDocuments(filter);

    // Apply pagination
    int skip = (pageNumber - 1) * pageSize;

    List<Document> documents = new ArrayList<>();
    try (MongoCursor<Document> cursor =
        collection.find(filter).skip(skip).limit(pageSize).iterator()) {
      while (cursor.hasNext()) {
        documents.add(cursor.next());
      }
    }

    logger.info("Found {} flexibilities (total: {})", documents.size(), totalCount);

    return new QueryResult(documents, totalCount);
  }

  public int bulkInsert(List<Flexibility> flexibilities, String orgCode) {
    if (flexibilities.isEmpty()) {
      logger.warn("No flexibilities to insert");
      return 0;
    }

    try {
      List<Document> documents =
          flexibilities.stream()
              .map(flex -> toDocument(flex, orgCode))
              .collect(Collectors.toList());

      InsertManyOptions options = new InsertManyOptions().ordered(false);
      collection.insertMany(documents, options);

      logger.info(
          "Successfully inserted {} flexibilities for orgCode: {}", documents.size(), orgCode);

      return documents.size();

    } catch (Exception e) {
      logger.error("Error during bulk insert. OrgCode: {}", orgCode, e);
      throw new RuntimeException("Failed to bulk insert flexibilities", e);
    }
  }

  private Document toDocument(Flexibility flexibility, String orgCode) {
    Document doc =
        new Document()
            .append("flexibilityId", flexibility.getFlexibilityId())
            .append("orgCode", orgCode)
            .append("createdAt", System.currentTimeMillis());

    // Add optional fields only if they're not null
    if (flexibility.getFlexibilityName() != null) {
      doc.append("flexibilityName", flexibility.getFlexibilityName());
    }
    if (flexibility.getFlexibilityPower() != null) {
      doc.append("flexibilityPower", flexibility.getFlexibilityPower());
    }
    if (flexibility.getFlexibilityType() != null) {
      doc.append("flexibilityType", flexibility.getFlexibilityType());
    }
    if (flexibility.getFlexibilityValidFrom() != null) {
      doc.append(
          "flexibilityValidFrom",
          flexibility
              .getFlexibilityValidFrom()
              .atStartOfDay()
              .atZone(java.time.ZoneId.systemDefault())
              .toInstant()
              .toEpochMilli());
    }
    if (flexibility.getFlexibilityValidTo() != null) {
      doc.append(
          "flexibilityValidTo",
          flexibility
              .getFlexibilityValidTo()
              .atStartOfDay()
              .atZone(java.time.ZoneId.systemDefault())
              .toInstant()
              .toEpochMilli());
    }
    if (flexibility.getTenant() != null) {
      doc.append("tenant", flexibility.getTenant());
    }
    if (flexibility.getHesInstanceId() != null) {
      doc.append("hesInstanceId", flexibility.getHesInstanceId());
    }
    if (flexibility.getControlDeviceId() != null) {
      doc.append("controlDeviceId", flexibility.getControlDeviceId());
    }
    if (flexibility.getControlDeviceType() != null) {
      doc.append("controlDeviceType", flexibility.getControlDeviceType());
    }
    if (flexibility.getControlDeviceInstallationTime() != null) {
      doc.append(
          "controlDeviceInstallationTime",
          flexibility
              .getControlDeviceInstallationTime()
              .atStartOfDay()
              .atZone(java.time.ZoneId.systemDefault())
              .toInstant()
              .toEpochMilli());
    }
    if (flexibility.getControlRelayId() != null) {
      doc.append("controlRelayId", flexibility.getControlRelayId());
    }
    if (flexibility.getCrmGroup() != null) {
      doc.append("crmGroup", flexibility.getCrmGroup());
    }
    if (flexibility.getMeteringPointName() != null) {
      doc.append("meteringPointName", flexibility.getMeteringPointName());
    }
    if (flexibility.getStreetName() != null) {
      doc.append("streetName", flexibility.getStreetName());
    }
    if (flexibility.getStreetNumber() != null) {
      doc.append("streetNumber", flexibility.getStreetNumber());
    }
    if (flexibility.getPostCode() != null) {
      doc.append("postCode", flexibility.getPostCode());
    }
    if (flexibility.getCity() != null) {
      doc.append("city", flexibility.getCity());
    }

    // Add metadata
    doc.append("rowNumber", flexibility.getRowNumber());

    return doc;
  }

  /**
   * Convert MongoDB Document to Flexibility domain object
   *
   * @param doc MongoDB document
   * @return Flexibility domain object
   */
  public Flexibility fromDocument(Document doc) {
    Flexibility.Builder builder =
        Flexibility.builder().flexibilityId(doc.getString("flexibilityId"));

    // Map optional string fields
    if (doc.containsKey("flexibilityName")) {
      builder.flexibilityName(doc.getString("flexibilityName"));
    }
    if (doc.containsKey("flexibilityType")) {
      builder.flexibilityType(doc.getString("flexibilityType"));
    }
    if (doc.containsKey("tenant")) {
      builder.tenant(doc.getString("tenant"));
    }
    if (doc.containsKey("hesInstanceId")) {
      builder.hesInstanceId(doc.getString("hesInstanceId"));
    }
    if (doc.containsKey("controlDeviceId")) {
      builder.controlDeviceId(doc.getString("controlDeviceId"));
    }
    if (doc.containsKey("controlDeviceType")) {
      builder.controlDeviceType(doc.getString("controlDeviceType"));
    }
    if (doc.containsKey("crmGroup")) {
      builder.crmGroup(doc.getString("crmGroup"));
    }
    if (doc.containsKey("meteringPointName")) {
      builder.meteringPointName(doc.getString("meteringPointName"));
    }
    if (doc.containsKey("streetName")) {
      builder.streetName(doc.getString("streetName"));
    }
    if (doc.containsKey("streetNumber")) {
      builder.streetNumber(doc.getString("streetNumber"));
    }
    if (doc.containsKey("postCode")) {
      builder.postCode(doc.getString("postCode"));
    }
    if (doc.containsKey("city")) {
      builder.city(doc.getString("city"));
    }

    // Map integer fields
    if (doc.containsKey("flexibilityPower")) {
      builder.flexibilityPower(doc.getInteger("flexibilityPower"));
    }
    if (doc.containsKey("controlRelayId")) {
      builder.controlRelayId(doc.getInteger("controlRelayId"));
    }

    // Map date fields (convert from epoch millis to LocalDate)
    if (doc.containsKey("flexibilityValidFrom")) {
      Long epochMillis = doc.getLong("flexibilityValidFrom");
      if (epochMillis != null) {
        builder.flexibilityValidFrom(
            java.time.Instant.ofEpochMilli(epochMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate());
      }
    }
    if (doc.containsKey("flexibilityValidTo")) {
      Long epochMillis = doc.getLong("flexibilityValidTo");
      if (epochMillis != null) {
        builder.flexibilityValidTo(
            java.time.Instant.ofEpochMilli(epochMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate());
      }
    }
    if (doc.containsKey("controlDeviceInstallationTime")) {
      Long epochMillis = doc.getLong("controlDeviceInstallationTime");
      if (epochMillis != null) {
        builder.controlDeviceInstallationTime(
            java.time.Instant.ofEpochMilli(epochMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate());
      }
    }

    // Map metadata
    if (doc.containsKey("rowNumber")) {
      builder.rowNumber(doc.getInteger("rowNumber", 0));
    }

    return builder.build();
  }
}
