package org.example.dto;

public class BranchResponse {

    private Long id;
    private String pincode;
    private String city;
    private String cityCode;

    public String getBusinessVertical() {
        return businessVertical;
    }

    public void setBusinessVertical(String businessVertical) {
        this.businessVertical = businessVertical;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchType() {
        return branchType;
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    public String getPennantBranchCode() {
        return pennantBranchCode;
    }

    public void setPennantBranchCode(String pennantBranchCode) {
        this.pennantBranchCode = pennantBranchCode;
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    private String billingState;
    private String stateCode;
    private String branchName;
    private String branchType;
    private String pennantBranchCode;
    private String businessVertical;
    private String productNames;

    public BranchResponse() {
    }

    // Generate Getters and Setters
}
