package domain;

import javax.persistence.*;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    private PlantClass plantClass;

    @ManyToOne
    private Environment environment;

    private String description;

    private Integer hp;

    private Integer dmg;

    private Integer sunCost;

    private String actionSpeed;

    public Plant() {
    }

    public Plant(Long id, String name, PlantClass plantClass, Environment environment, String description, Integer hp, Integer dmg, Integer sunCost, String actionSpeed) {
        this.id = id;
        this.name = name;
        this.plantClass = plantClass;
        this.environment = environment;
        this.description = description;
        this.hp = hp;
        this.dmg = dmg;
        this.sunCost = sunCost;
        this.actionSpeed = actionSpeed;
    }

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

    public PlantClass getPlantClass() {
        return plantClass;
    }

    public void setPlantClass(PlantClass plantClass) {
        this.plantClass = plantClass;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
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
}
