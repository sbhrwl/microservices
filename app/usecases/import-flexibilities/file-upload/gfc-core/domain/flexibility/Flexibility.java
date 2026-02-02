package com.landisgyr.gfc.domain.flexibility;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Flexibility {
  // Mandatory field
  private String flexibilityId;

  // CSV fields
  private String flexibilityName;
  private Integer flexibilityPower;
  private String flexibilityType;
  private LocalDate flexibilityValidFrom;
  private LocalDate flexibilityValidTo;
  private String tenant;
  private String hesInstanceId;
  private String controlDeviceId;
  private String controlDeviceType;
  private LocalDate controlDeviceInstallationTime;
  private Integer controlRelayId;
  private String crmGroup;
  private String meteringPointName;
  private String streetName;
  private String streetNumber;
  private String postCode;
  private String city;

  // Metadata
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private int rowNumber; // Track which CSV row this came from

  // Private constructor for builder
  private Flexibility() {}

  // Builder pattern
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final Flexibility flexibility;

    private Builder() {
      this.flexibility = new Flexibility();
    }

    public Builder flexibilityId(String flexibilityId) {
      this.flexibility.flexibilityId = flexibilityId;
      return this;
    }

    public Builder flexibilityName(String flexibilityName) {
      this.flexibility.flexibilityName = flexibilityName;
      return this;
    }

    public Builder flexibilityPower(Integer flexibilityPower) {
      this.flexibility.flexibilityPower = flexibilityPower;
      return this;
    }

    public Builder flexibilityType(String flexibilityType) {
      this.flexibility.flexibilityType = flexibilityType;
      return this;
    }

    public Builder flexibilityValidFrom(LocalDate flexibilityValidFrom) {
      this.flexibility.flexibilityValidFrom = flexibilityValidFrom;
      return this;
    }

    public Builder flexibilityValidTo(LocalDate flexibilityValidTo) {
      this.flexibility.flexibilityValidTo = flexibilityValidTo;
      return this;
    }

    public Builder tenant(String tenant) {
      this.flexibility.tenant = tenant;
      return this;
    }

    public Builder hesInstanceId(String hesInstanceId) {
      this.flexibility.hesInstanceId = hesInstanceId;
      return this;
    }

    public Builder controlDeviceId(String controlDeviceId) {
      this.flexibility.controlDeviceId = controlDeviceId;
      return this;
    }

    public Builder controlDeviceType(String controlDeviceType) {
      this.flexibility.controlDeviceType = controlDeviceType;
      return this;
    }

    public Builder controlDeviceInstallationTime(LocalDate controlDeviceInstallationTime) {
      this.flexibility.controlDeviceInstallationTime = controlDeviceInstallationTime;
      return this;
    }

    public Builder controlRelayId(Integer controlRelayId) {
      this.flexibility.controlRelayId = controlRelayId;
      return this;
    }

    public Builder crmGroup(String crmGroup) {
      this.flexibility.crmGroup = crmGroup;
      return this;
    }

    public Builder meteringPointName(String meteringPointName) {
      this.flexibility.meteringPointName = meteringPointName;
      return this;
    }

    public Builder streetName(String streetName) {
      this.flexibility.streetName = streetName;
      return this;
    }

    public Builder streetNumber(String streetNumber) {
      this.flexibility.streetNumber = streetNumber;
      return this;
    }

    public Builder postCode(String postCode) {
      this.flexibility.postCode = postCode;
      return this;
    }

    public Builder city(String city) {
      this.flexibility.city = city;
      return this;
    }

    public Builder rowNumber(int rowNumber) {
      this.flexibility.rowNumber = rowNumber;
      return this;
    }

    public Flexibility build() {
      // Validate mandatory field
      if (this.flexibility.flexibilityId == null
          || this.flexibility.flexibilityId.trim().isEmpty()) {
        throw new IllegalArgumentException(
            "FlexibilityId is mandatory and cannot be null or empty");
      }

      this.flexibility.createdAt = LocalDateTime.now();
      this.flexibility.updatedAt = LocalDateTime.now();
      return this.flexibility;
    }
  }

  // Getters
  public String getFlexibilityId() {
    return flexibilityId;
  }

  public String getFlexibilityName() {
    return flexibilityName;
  }

  public Integer getFlexibilityPower() {
    return flexibilityPower;
  }

  public String getFlexibilityType() {
    return flexibilityType;
  }

  public LocalDate getFlexibilityValidFrom() {
    return flexibilityValidFrom;
  }

  public LocalDate getFlexibilityValidTo() {
    return flexibilityValidTo;
  }

  public String getTenant() {
    return tenant;
  }

  public String getHesInstanceId() {
    return hesInstanceId;
  }

  public String getControlDeviceId() {
    return controlDeviceId;
  }

  public String getControlDeviceType() {
    return controlDeviceType;
  }

  public LocalDate getControlDeviceInstallationTime() {
    return controlDeviceInstallationTime;
  }

  public Integer getControlRelayId() {
    return controlRelayId;
  }

  public String getCrmGroup() {
    return crmGroup;
  }

  public String getMeteringPointName() {
    return meteringPointName;
  }

  public String getStreetName() {
    return streetName;
  }

  public String getStreetNumber() {
    return streetNumber;
  }

  public String getPostCode() {
    return postCode;
  }

  public String getCity() {
    return city;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public int getRowNumber() {
    return rowNumber;
  }
}
