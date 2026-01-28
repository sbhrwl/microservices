package com.landisgyr.gfc.grpc;

import static com.landisgyr.gfc.keycloak.SecurityUtils.validateAccess;

import com.google.protobuf.FieldMask;
import com.landisgyr.gfc.api.v1.flexibility.FlexibilityPb;
import com.landisgyr.gfc.api.v1.flexibility.FlexibilityServiceGrpc;
import com.landisgyr.gfc.config.ApplicationSetting;
import com.landisgyr.gfc.domain.common.GenericPage;
import com.landisgyr.gfc.domain.device.*;
import com.landisgyr.gfc.domain.organization.DeviceIdentifierType;
import com.landisgyr.gfc.domain.query.Sorting;
import com.landisgyr.gfc.domain.query.filters.SearchFilter;
import com.landisgyr.gfc.exceptions.InvalidRequestException;
import com.landisgyr.gfc.grpc.support.FieldMaskUtils;
import com.landisgyr.gfc.services.*;
import dagger.grpc.server.GrpcService;
import io.grpc.stub.StreamObserver;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import javax.inject.Inject;

@GrpcService(grpcClass = FlexibilityServiceGrpc.class)
public class FlexibilityServiceImpl extends FlexibilityServiceGrpc.FlexibilityServiceImplBase {
  private final FlexibilityQueryService flexibilityQueryService;
  private final FlexibilityRegistrationService deviceRegistrationService;
  private final FlexibilityMutationService flexibilityMutationService;

  private static final Logger logger = LoggerFactory.getLogger(FlexibilityServiceImpl.class);

  private final ApplicationSetting.FeatureFlags featureFlags;

  public static final String COMMA = ",";

  public static final Character DOUBLE_QUOTE_CHAR = '"';

  @Inject
  public FlexibilityServiceImpl(
      FlexibilityQueryService flexibilityQueryService,
      FlexibilityRegistrationService deviceRegistrationService,
      FlexibilityMutationService flexibilityMutationService,
      ApplicationSetting.FeatureFlags featureFlags) {
    this.flexibilityQueryService = flexibilityQueryService;
    this.deviceRegistrationService = deviceRegistrationService;
    this.flexibilityMutationService = flexibilityMutationService;

    // TODO:
    // https://gitlab.cicd.landisgyr.com/landisgyr/rnd/emea/greenfield-gfc/emea-gfc-dev-app/-/issues/464
    this.featureFlags = featureFlags;
  }

  @Override
  public void queryFlexibilities(
      FlexibilityPb.QueryFlexibilitiesRequest request,
      StreamObserver<FlexibilityPb.QueryFlexibilitiesResponse> responseObserver) {

    logger.info("Querying flexibilities");

    try {
      // Create mock flexibilities with realistic data
      List<FlexibilityPb.Flexibility> flexibilities = new ArrayList<>();

      // Define flexibility types
      String[] types = {
        "xxBoiler", "Lighting", "Heat pump", "Beleuchtung", "Wärmepumpe", "Elektroauto", "PV"
      };
      String[] baseIds = {"53851129", "53851130", "53851131", "53851142", "53851143", "53851163"};

      // Generate flexibilities for base IDs with suffixes
      for (String baseId : baseIds) {
        int count =
            baseId.equals("53851142") || baseId.equals("53851143") || baseId.equals("53851163")
                ? 1
                : 4;

        for (int i = 1; i <= count; i++) {
          String id = baseId + "_" + i;
          String name = "L" + id;
          String type = getFlexibilityType(baseId, i);

          FlexibilityPb.Flexibility flexibility =
              FlexibilityPb.Flexibility.newBuilder()
                  .setId(id)
                  .setName(name)
                  .setFlexibilityType(type)
                  .build();

          flexibilities.add(flexibility);
        }
      }

      // Add PV flexibilities
      for (int i = 1; i <= 2; i++) {
        String id = String.format("%02d_PV", i);
        FlexibilityPb.Flexibility flexibility =
            FlexibilityPb.Flexibility.newBuilder()
                .setId(id)
                .setName(id)
                .setFlexibilityType("PV")
                .build();

        flexibilities.add(flexibility);
      }

      FlexibilityPb.Flexibilities.Builder builder = FlexibilityPb.Flexibilities.newBuilder();
      builder.addAllItems(flexibilities);

      FlexibilityPb.QueryFlexibilitiesResponse response =
          FlexibilityPb.QueryFlexibilitiesResponse.newBuilder()
              .setFlexibilities(builder.build())
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      logger.error("Error querying flexibilities", e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void getFlexibility(
      FlexibilityPb.GetFlexibilityRequest request,
      StreamObserver<FlexibilityPb.GetFlexibilityResponse> responseObserver) {
    super.getFlexibility(request, responseObserver);
  }

  @Override
  public void uploadFlexibilities(
      FlexibilityPb.UploadCsvRequest request,
      StreamObserver<FlexibilityPb.UploadCsvResponse> responseObserver) {

    logger.info(
        "Received Flexibilities file: {}",
        new String(
            request
                .getContent()
                .substring(0, Math.min(128, request.getContent().size()))
                .toByteArray()));

    String uploadId = UUID.randomUUID().toString();

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
                .get()
                .parse(reader)) {

      List<String> uniqueColumns = List.of("FlexibilityId", "Tenant");

      if (!new HashSet<>(csvParser.getHeaderNames()).containsAll(uniqueColumns)) {
        logger.warn(
            "Received: {}",
            new String(
                request
                    .getContent()
                    .substring(0, Math.min(128, request.getContent().size()))
                    .toByteArray()));

        throw new InvalidRequestException(
            "Missing unique columns. Expected=%s. Actual=%s",
            StringUtils.join(uniqueColumns), StringUtils.join(csvParser.getHeaderNames()));
      }

    } catch (IOException e) {
      throw new InvalidRequestException("Error parsing cvs", e);
    }

    FlexibilityPb.CsvSummary.Builder summary =
        FlexibilityPb.CsvSummary.newBuilder()
                .setTotalRows(100)
                .setInvalidRows(12)
            .setFileMetadata(
                FlexibilityPb.FileMetadata.newBuilder()
                    .setFilename(request.getFilename())
                    .setFileSizeBytes(request.getContent().size())
                    .setUploadedAt(
                        com.google.protobuf.Timestamp.newBuilder()
                            .setSeconds(System.currentTimeMillis() / 1000)
                            .setNanos((int) ((System.currentTimeMillis() % 1000) * 1000000))
                            .build())
                    .build())
            .addAllFlexibilityTypeCounts(
                Arrays.asList(
                    FlexibilityPb.FlexibilityTypeCount.newBuilder()
                        .setFlexibilityType("Boiler")
                        .setCount(45)
                        .build(),
                    FlexibilityPb.FlexibilityTypeCount.newBuilder()
                        .setFlexibilityType("Heat pump")
                        .setCount(32)
                        .build(),
                    FlexibilityPb.FlexibilityTypeCount.newBuilder()
                        .setFlexibilityType("Lighting")
                        .setCount(28)
                        .build(),
                    FlexibilityPb.FlexibilityTypeCount.newBuilder()
                        .setFlexibilityType("PV")
                        .setCount(18)
                        .build()))
            .setErrorDetails(
                FlexibilityPb.ErrorDetails.newBuilder()
                    .addAllErrors(
                        Arrays.asList(
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(5)
                                .setColumnName("FlexibilityId")
                                .setErrorMessage("Duplicate FlexibilityId found: FLEX-001")
                                .build(),
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(12)
                                .setColumnName("Tenant")
                                .setErrorMessage("Invalid tenant format: expected alphanumeric")
                                .build(),
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(18)
                                .setColumnName("FlexibilityId")
                                .setErrorMessage("Duplicate FlexibilityId found: FLEX-042")
                                .build(),
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(23)
                                .setColumnName("FlexibilityType")
                                .setErrorMessage("Unknown flexibility type: InvalidType")
                                .build(),
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(31)
                                .setColumnName("Name")
                                .setErrorMessage("Missing required field: Name")
                                .build(),
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(45)
                                .setColumnName("FlexibilityId")
                                .setErrorMessage("Duplicate FlexibilityId found: FLEX-078")
                                .build(),
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(67)
                                .setColumnName("Capacity")
                                .setErrorMessage("Invalid format: expected numeric value")
                                .build(),
                            FlexibilityPb.RowError.newBuilder()
                                .setRowNumber(89)
                                .setColumnName("Location")
                                .setErrorMessage("Missing required field: Location")
                                .build()))
                    .build());

    FlexibilityPb.UploadCsvResponse response =
        FlexibilityPb.UploadCsvResponse.newBuilder()
            .setUploadId(uploadId)
            .setCsvSummary(summary.build())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void confirmUploadFlexibilities(
      FlexibilityPb.ConfirmUploadFlexibilitiesRequest request,
      StreamObserver<FlexibilityPb.ConfirmUploadFlexibilitiesResponse> responseObserver) {
    logger.info("confirmUploadFlexibilities for uploadId: {}", request.getUploadId());
    
    FlexibilityPb.ConfirmUploadFlexibilitiesResponse response =
        FlexibilityPb.ConfirmUploadFlexibilitiesResponse.newBuilder()
            .setUploadId(request.getUploadId())
            .setImportSummary(
                FlexibilityPb.ImportSummary.newBuilder()
                    .setTotalRows(100)
                    .setImportedRows(88)
                    .setFailedRows(12)
                    .setErrorDetails(
                        FlexibilityPb.ErrorDetails.newBuilder()
                            .addAllErrors(
                                Arrays.asList(
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(5)
                                        .setColumnName("FlexibilityId")
                                        .setErrorMessage("Duplicate FlexibilityId found: FLEX-001")
                                        .build(),
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(12)
                                        .setColumnName("Tenant")
                                        .setErrorMessage("Invalid tenant format: expected alphanumeric")
                                        .build(),
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(18)
                                        .setColumnName("FlexibilityId")
                                        .setErrorMessage("Duplicate FlexibilityId found: FLEX-042")
                                        .build(),
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(23)
                                        .setColumnName("FlexibilityType")
                                        .setErrorMessage("Unknown flexibility type: InvalidType")
                                        .build(),
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(31)
                                        .setColumnName("Name")
                                        .setErrorMessage("Missing required field: Name")
                                        .build(),
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(45)
                                        .setColumnName("FlexibilityId")
                                        .setErrorMessage("Duplicate FlexibilityId found: FLEX-078")
                                        .build(),
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(67)
                                        .setColumnName("Capacity")
                                        .setErrorMessage("Invalid format: expected numeric value")
                                        .build(),
                                    FlexibilityPb.RowError.newBuilder()
                                        .setRowNumber(89)
                                        .setColumnName("Location")
                                        .setErrorMessage("Missing required field: Location")
                                        .build()))
                            .build())
                    .build())
            .build();
    
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private String getFlexibilityType(String baseId, int index) {
    // Map flexibility types based on baseId and index
    switch (baseId) {
      case "53851129":
      case "53851130":
        switch (index) {
          case 1:
            return "Boiler";
          case 2:
            return "Lighting";
          case 3:
            return "Boiler";
          case 4:
            return "Heat pump";
        }
        break;
      case "53851131":
        switch (index) {
          case 1:
            return "Boiler";
          case 2:
            return "Beleuchtung";
          case 3:
            return "Boiler";
          case 4:
            return "Wärmepumpe";
        }
        break;
      case "53851142":
        switch (index) {
          case 1:
            return "Boiler";
          case 2:
            return "Beleuchtung";
        }
        break;
      case "53851143":
      case "53851163":
        return "Elektroauto";
    }
    return "Unknown";
  }

  private int getOrDefault(int value, int defaultValue) {
    return (value == 0) ? defaultValue : value;
  }

  private List<String> toProjectionFields(List<String> fieldNamesList) {
    List<String> updatedList = new ArrayList<>();

    for (String value : fieldNamesList) {
      if (value.equals("deviceAliasId")) {
        updatedList.addAll(
            List.of(
                DeviceIdentifierType.SERIAL_NUMBER.getCode(),
                DeviceIdentifierType.UTILITY_SERIAL_NUMBER.getCode()));
      } else {
        updatedList.add(value);
      }
    }
    return updatedList;
  }
}
