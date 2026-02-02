package com.landisgyr.gfc.domain.flexibility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlexibilityCsvParser {
  private static final Logger logger = LoggerFactory.getLogger(FlexibilityCsvParser.class);

  // Date format from CSV: DD-MM-YYYY
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  /**
   * Parse a CSV record into a Flexibility object
   *
   * @param record CSV record
   * @param rowNumber Row number for tracking
   * @return Flexibility object
   * @throws IllegalArgumentException if FlexibilityId is missing
   */
  public static Flexibility parseRecord(CSVRecord record, int rowNumber) {
    Flexibility.Builder builder = Flexibility.builder().rowNumber(rowNumber);

    // Mandatory field
    builder.flexibilityId(getStringValue(record, "FlexibilityId"));

    // Optional fields
    builder.flexibilityName(getStringValue(record, "FlexibilityName"));
    builder.flexibilityPower(getIntegerValue(record, "FlexibilityPower"));
    builder.flexibilityType(getStringValue(record, "FlexibilityType"));
    builder.flexibilityValidFrom(getDateValue(record, "FlexibilityValidFrom"));
    builder.flexibilityValidTo(getDateValue(record, "FlexibilityValidTo"));
    builder.tenant(getStringValue(record, "Tenant"));
    builder.hesInstanceId(getStringValue(record, "HESInstanceId"));
    builder.controlDeviceId(getStringValue(record, "ControlDeviceId"));
    builder.controlDeviceType(getStringValue(record, "ControlDeviceType"));
    builder.controlDeviceInstallationTime(getDateValue(record, "ControlDeviceInstallationTime"));
    builder.controlRelayId(getIntegerValue(record, "ControlRelayId"));
    builder.crmGroup(getStringValue(record, "CRMGroup"));
    builder.meteringPointName(getStringValue(record, "MeteringPointName"));
    builder.streetName(getStringValue(record, "StreetName"));
    builder.streetNumber(getStringValue(record, "StreetNumber"));
    builder.postCode(getStringValue(record, "PostCode"));
    builder.city(getStringValue(record, "City"));

    return builder.build();
  }

  /** Get string value from CSV record, returns null if empty */
  private static String getStringValue(CSVRecord record, String columnName) {
    try {
      String value = record.get(columnName);
      return StringUtils.isBlank(value) ? null : value.trim();
    } catch (IllegalArgumentException e) {
      logger.warn("Column '{}' not found in CSV", columnName);
      return null;
    }
  }

  /** Get integer value from CSV record, returns null if empty or invalid */
  private static Integer getIntegerValue(CSVRecord record, String columnName) {
    String value = getStringValue(record, columnName);
    if (value == null) {
      return null;
    }

    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      logger.warn(
          "Invalid integer value '{}' for column '{}' at row {}",
          value,
          columnName,
          record.getRecordNumber());
      return null;
    }
  }

  /** Get date value from CSV record (DD-MM-YYYY format), returns null if empty or invalid */
  private static LocalDate getDateValue(CSVRecord record, String columnName) {
    String value = getStringValue(record, columnName);
    if (value == null) {
      return null;
    }

    try {
      return LocalDate.parse(value, DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      logger.warn(
          "Invalid date value '{}' for column '{}' at row {}. Expected format: DD-MM-YYYY",
          value,
          columnName,
          record.getRecordNumber());
      return null;
    }
  }
}
