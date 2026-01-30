package com.landisgyr.gfc.grpc;

import com.google.protobuf.Timestamp;
import com.landisgyr.gfc.api.v1.flexibility.FlexibilityPb;
import com.landisgyr.gfc.api.v1.flexibility.FlexibilityServiceGrpc;
import com.landisgyr.gfc.config.ApplicationSetting;
import com.landisgyr.gfc.dao.FlexibilityRsDao;
import com.landisgyr.gfc.domain.flexibility.Flexibility;
import com.landisgyr.gfc.domain.flexibility.FlexibilityCsvParser;
import com.landisgyr.gfc.exceptions.InvalidRequestException;
import com.landisgyr.gfc.services.*;
import dagger.grpc.server.GrpcService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService(grpcClass = FlexibilityServiceGrpc.class)
public class FlexibilityServiceImpl extends FlexibilityServiceGrpc.FlexibilityServiceImplBase {
  private final FlexibilityQueryService flexibilityQueryService;
  private final FlexibilityRegistrationService deviceRegistrationService;
  private final FlexibilityMutationService flexibilityMutationService;
  private final FlexibilityRsDao flexibilityRsDao;
  private final FlexibilityUploadService uploadService;

  private static final Logger logger = LoggerFactory.getLogger(FlexibilityServiceImpl.class);

  private final ApplicationSetting.FeatureFlags featureFlags;

  public static final String COMMA = ",";
  public static final Character DOUBLE_QUOTE_CHAR = '"';

  @Inject
  public FlexibilityServiceImpl(
      FlexibilityQueryService flexibilityQueryService,
      FlexibilityRegistrationService deviceRegistrationService,
      FlexibilityMutationService flexibilityMutationService,
      FlexibilityRsDao flexibilityRsDao,
      FlexibilityUploadService uploadService,
      ApplicationSetting.FeatureFlags featureFlags) {
    this.flexibilityQueryService = flexibilityQueryService;
    this.deviceRegistrationService = deviceRegistrationService;
    this.flexibilityMutationService = flexibilityMutationService;
    this.flexibilityRsDao = flexibilityRsDao;
    this.uploadService = uploadService;
    this.featureFlags = featureFlags;
  }

  @Override
  public void queryFlexibilities(
      FlexibilityPb.QueryFlexibilitiesRequest request,
      StreamObserver<FlexibilityPb.QueryFlexibilitiesResponse> responseObserver) {

    logger.info("Querying flexibilities with filters: {}", request);

    try {
      // Extract filter parameters from request
      FlexibilityPb.FlexibilityQueryFilter filter =
          request.hasFilter()
              ? request.getFilter()
              : FlexibilityPb.FlexibilityQueryFilter.getDefaultInstance();

      List<String> flexibilityIdIn = filter.getFlexibilityIdInList();
      List<String> flexibilityNameIn = filter.getFlexibilityNameInList();
      List<String> flexibilityTypeIn = filter.getFlexibilityTypeInList();

      // Use default pagination values since proto doesn't have these fields yet
      int pageNumber = 1;
      int pageSize = 50;

      String orgCode = "GFC_CPE"; // TODO: Get from request context

      logger.info(
          "Query parameters - Page: {}, Size: {}, OrgCode: {}, Filters: [IdIn={}, NameIn={}, TypeIn={}]",
          pageNumber,
          pageSize,
          orgCode,
          flexibilityIdIn.size(),
          flexibilityNameIn.size(),
          flexibilityTypeIn.size());

      // Query from DAO
      FlexibilityRsDao.QueryResult queryResult =
          flexibilityRsDao.findFlexibilities(
              flexibilityIdIn.isEmpty() ? null : flexibilityIdIn,
              flexibilityNameIn.isEmpty() ? null : flexibilityNameIn,
              flexibilityTypeIn.isEmpty() ? null : flexibilityTypeIn,
              pageNumber,
              pageSize);

      // Convert MongoDB documents to Flexibility domain objects
      List<Flexibility> flexibilities =
          queryResult.getDocuments().stream()
              .map(flexibilityRsDao::fromDocument)
              .collect(Collectors.toList());

      logger.info(
          "Retrieved {} flexibilities from database (total: {})",
          flexibilities.size(),
          queryResult.getTotalCount());

      // Convert domain objects to protobuf messages
      List<FlexibilityPb.Flexibility> flexibilityProtos =
          flexibilities.stream().map(this::toFlexibilityProto).collect(Collectors.toList());

      // Build response with only the fields that exist in proto
      FlexibilityPb.Flexibilities flexibilitiesMsg =
          FlexibilityPb.Flexibilities.newBuilder().addAllItems(flexibilityProtos).build();

      FlexibilityPb.QueryFlexibilitiesResponse response =
          FlexibilityPb.QueryFlexibilitiesResponse.newBuilder()
              .setFlexibilities(flexibilitiesMsg)
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

      logger.info("Successfully returned {} flexibilities", flexibilityProtos.size());

    } catch (Exception e) {
      logger.error("Error querying flexibilities", e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to query flexibilities: " + e.getMessage())
              .withCause(e)
              .asRuntimeException());
    }
  }

  /**
   * Convert domain Flexibility to protobuf Flexibility. Only maps fields that exist in the proto
   * definition.
   */
  private FlexibilityPb.Flexibility toFlexibilityProto(Flexibility flexibility) {
    FlexibilityPb.Flexibility.Builder builder = FlexibilityPb.Flexibility.newBuilder();

    // Only set fields that definitely exist in proto
    if (flexibility.getFlexibilityId() != null) {
      builder.setId(flexibility.getFlexibilityId());
    }

    if (flexibility.getFlexibilityName() != null) {
      builder.setName(flexibility.getFlexibilityName());
    }

    if (flexibility.getFlexibilityType() != null) {
      builder.setFlexibilityType(flexibility.getFlexibilityType());
    }

    // TODO: Add more field mappings once proto is updated with all fields

    return builder.build();
  }

  @Override
  public void uploadFlexibilities(
      FlexibilityPb.UploadCsvRequest request,
      StreamObserver<FlexibilityPb.UploadCsvResponse> responseObserver) {

    logger.info("Received Flexibilities file: {}", request.getFilename());

    String uploadId = UUID.randomUUID().toString();
    String orgCode = "GFC_CPE"; // TODO: Get from request context

    List<Flexibility> validFlexibilities = new ArrayList<>();
    List<FlexibilityPb.RowError> errors = new ArrayList<>();
    Map<String, Integer> typeCountMap = new HashMap<>();
    int totalRows = 0;

    try (Reader reader = new InputStreamReader(request.getContent().newInput());
        CSVParser csvParser =
            CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setDelimiter(COMMA)
                .setEscape(DOUBLE_QUOTE_CHAR)
                .setQuote(DOUBLE_QUOTE_CHAR)
                .setQuoteMode(QuoteMode.MINIMAL)
                .setTrim(true)
                .build()
                .parse(reader)) {

      // Validate required columns exist
      List<String> requiredColumns = List.of("FlexibilityId");
      if (!new HashSet<>(csvParser.getHeaderNames()).containsAll(requiredColumns)) {
        throw new InvalidRequestException(
            "Missing required columns. Expected=%s. Actual=%s",
            StringUtils.join(requiredColumns), StringUtils.join(csvParser.getHeaderNames()));
      }

      // Parse each CSV row
      for (CSVRecord record : csvParser) {
        totalRows++;
        int rowNumber = (int) record.getRecordNumber();

        try {
          Flexibility flexibility = FlexibilityCsvParser.parseRecord(record, rowNumber);
          validFlexibilities.add(flexibility);

          String type = flexibility.getFlexibilityType();
          if (type != null) {
            typeCountMap.merge(type, 1, Integer::sum);
          }

        } catch (IllegalArgumentException e) {
          errors.add(
              FlexibilityPb.RowError.newBuilder()
                  .setRowNumber(rowNumber)
                  .setColumnName("FlexibilityId")
                  .setErrorMessage(e.getMessage())
                  .build());
        }
      }

      logger.info(
          "CSV parsing completed. Total rows: {}, Valid: {}, Invalid: {}",
          totalRows,
          validFlexibilities.size(),
          errors.size());

      // Store the CSV file for later confirmation
      uploadService.storeUpload(
          uploadId, request.getFilename(), request.getContent().toByteArray(), orgCode);

    } catch (IOException e) {
      throw new InvalidRequestException("Error parsing csv", e);
    }

    // Build flexibility type counts
    List<FlexibilityPb.FlexibilityTypeCount> typeCounts =
        typeCountMap.entrySet().stream()
            .map(
                entry ->
                    FlexibilityPb.FlexibilityTypeCount.newBuilder()
                        .setFlexibilityType(entry.getKey())
                        .setCount(entry.getValue())
                        .build())
            .collect(Collectors.toList());

    // Build error details
    FlexibilityPb.ErrorDetails errorDetails =
        FlexibilityPb.ErrorDetails.newBuilder().addAllErrors(errors).build();

    // Build CSV summary
    FlexibilityPb.CsvSummary summary =
        FlexibilityPb.CsvSummary.newBuilder()
            .setTotalRows(totalRows)
            .setInvalidRows(errors.size())
            .setFileMetadata(
                FlexibilityPb.FileMetadata.newBuilder()
                    .setFilename(request.getFilename())
                    .setFileSizeBytes(request.getContent().size())
                    .setUploadedAt(
                        Timestamp.newBuilder()
                            .setSeconds(System.currentTimeMillis() / 1000)
                            .build())
                    .build())
            .addAllFlexibilityTypeCounts(typeCounts)
            .setErrorDetails(errorDetails)
            .build();

    FlexibilityPb.UploadCsvResponse response =
        FlexibilityPb.UploadCsvResponse.newBuilder()
            .setUploadId(uploadId)
            .setCsvSummary(summary)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void confirmUploadFlexibilities(
      FlexibilityPb.ConfirmUploadFlexibilitiesRequest request,
      StreamObserver<FlexibilityPb.ConfirmUploadFlexibilitiesResponse> responseObserver) {

    logger.info("confirmUploadFlexibilities for uploadId: {}", request.getUploadId());

    String uploadId = request.getUploadId();

    try {
      // Retrieve the stored CSV file
      byte[] csvContent = uploadService.retrieveUpload(uploadId);
      String orgCode = uploadService.getOrgCode(uploadId);

      logger.info(
          "Retrieved CSV for uploadId: {}. Size: {} bytes, OrgCode: {}",
          uploadId,
          csvContent.length,
          orgCode);

      // Parse CSV file again
      List<Flexibility> validFlexibilities = new ArrayList<>();
      List<FlexibilityPb.RowError> errors = new ArrayList<>();
      int totalRows = 0;

      try (Reader reader = new InputStreamReader(new java.io.ByteArrayInputStream(csvContent));
          CSVParser csvParser =
              CSVFormat.DEFAULT
                  .builder()
                  .setHeader()
                  .setSkipHeaderRecord(true)
                  .setDelimiter(COMMA)
                  .setEscape(DOUBLE_QUOTE_CHAR)
                  .setQuote(DOUBLE_QUOTE_CHAR)
                  .setQuoteMode(QuoteMode.MINIMAL)
                  .setTrim(true)
                  .build()
                  .parse(reader)) {

        List<String> requiredColumns = List.of("FlexibilityId");
        if (!new HashSet<>(csvParser.getHeaderNames()).containsAll(requiredColumns)) {
          throw new InvalidRequestException(
              "Missing required columns. Expected=%s. Actual=%s",
              StringUtils.join(requiredColumns), StringUtils.join(csvParser.getHeaderNames()));
        }

        for (CSVRecord record : csvParser) {
          totalRows++;
          int rowNumber = (int) record.getRecordNumber();

          try {
            Flexibility flexibility = FlexibilityCsvParser.parseRecord(record, rowNumber);
            validFlexibilities.add(flexibility);
          } catch (IllegalArgumentException e) {
            errors.add(
                FlexibilityPb.RowError.newBuilder()
                    .setRowNumber(rowNumber)
                    .setColumnName("FlexibilityId")
                    .setErrorMessage(e.getMessage())
                    .build());
          }
        }

        logger.info(
            "CSV re-parsing completed. Total rows: {}, Valid: {}, Invalid: {}",
            totalRows,
            validFlexibilities.size(),
            errors.size());
      }

      // Check for existing flexibilities
      List<String> flexibilityIds =
          validFlexibilities.stream()
              .map(Flexibility::getFlexibilityId)
              .collect(Collectors.toList());

      Set<String> existingIds =
          flexibilityRsDao.findExistingFlexibilityIds(flexibilityIds, orgCode);

      logger.info(
          "Duplicate check: {} flexibilities already exist in database", existingIds.size());

      // Separate new vs existing
      List<Flexibility> newFlexibilities =
          validFlexibilities.stream()
              .filter(f -> !existingIds.contains(f.getFlexibilityId()))
              .collect(Collectors.toList());

      logger.info("Import plan: {} new flexibilities", newFlexibilities.size());

      // Insert new flexibilities
      int insertedCount = 0;
      if (!newFlexibilities.isEmpty()) {
        try {
          insertedCount = flexibilityRsDao.bulkInsert(newFlexibilities, orgCode);
          logger.info("Successfully inserted {} new flexibilities", insertedCount);
        } catch (Exception e) {
          logger.error("Failed to insert flexibilities", e);
          errors.add(
              FlexibilityPb.RowError.newBuilder()
                  .setRowNumber(0)
                  .setColumnName("Database")
                  .setErrorMessage("Failed to insert flexibilities: " + e.getMessage())
                  .build());
        }
      }

      // Build response
      FlexibilityPb.ImportSummary.Builder summaryBuilder =
          FlexibilityPb.ImportSummary.newBuilder()
              .setTotalRows(totalRows)
              .setImportedRows(insertedCount)
              .setFailedRows(errors.size());

      if (!errors.isEmpty()) {
        summaryBuilder.setErrorDetails(
            FlexibilityPb.ErrorDetails.newBuilder().addAllErrors(errors).build());
      }

      FlexibilityPb.ConfirmUploadFlexibilitiesResponse response =
          FlexibilityPb.ConfirmUploadFlexibilitiesResponse.newBuilder()
              .setUploadId(uploadId)
              .setImportSummary(summaryBuilder.build())
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
      logger.info("Import completed. Inserted: {}, Failed: {}", insertedCount, errors.size());

    } catch (Exception e) {
      logger.error("Error during confirmUploadFlexibilities", e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription("Failed to import flexibilities: " + e.getMessage())
              .withCause(e)
              .asRuntimeException());
    }
  }
}
