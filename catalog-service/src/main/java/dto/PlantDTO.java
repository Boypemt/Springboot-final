package dto;

public class PlantDTO {
    private Long id;
    private String name;
    private Long classId;
    private String className;
    private Long environmentId;
    private String environmentName;
    private String description;
    private Integer hp;
    private Integer dmg;
    private Integer sunCost;
    private String actionSpeed;
    private Integer servedBy; // เก็บหมายเลข Port (เช่น 8100/8101) สำหรับ Load Balancer Demo
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getClassId() {
        return classId;
    }
    public void setClassId(Long classId) {
        this.classId = classId;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public Long getEnvironmentId() {
        return environmentId;
    }
    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }
    public String getEnvironmentName() {
        return environmentName;
    }
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getHp() {
        return hp;
    }
    public void setHp(Integer hp) {
        this.hp = hp;
    }
    public Integer getDmg() {
        return dmg;
    }
    public void setDmg(Integer dmg) {
        this.dmg = dmg;
    }
    public Integer getSunCost() {
        return sunCost;
    }
    public void setSunCost(Integer sunCost) {
        this.sunCost = sunCost;
    }
    public String getActionSpeed() {
        return actionSpeed;
    }
    public void setActionSpeed(String actionSpeed) {
        this.actionSpeed = actionSpeed;
    }
    public Integer getServedBy() {
        return servedBy;
    }
    public void setServedBy(Integer servedBy) {
        this.servedBy = servedBy;
    }

    
}
