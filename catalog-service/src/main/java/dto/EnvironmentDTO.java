package dto;

public class EnvironmentDTO {
    private Long id;
    private String envname;
    private String description;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEnvname() {
        return envname;
    }
    public void setEnvname(String envname) {
        this.envname = envname;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}
