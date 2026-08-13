package th.mfu.pvz.customer.dto;

public class AddressDTO {
    private Long id;
    private String country;
    private String city;
    private String district;
    private String subDistrict;
    private String zipcode;
    private Boolean isDefault;
    public AddressDTO() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getSubDistrict() { return subDistrict; }
    public void setSubDistrict(String subDistrict) { this.subDistrict = subDistrict; }
    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
