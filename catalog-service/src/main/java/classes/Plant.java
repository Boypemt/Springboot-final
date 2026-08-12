package classes;

import javax.persistence.*;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private PlantClass plantClass;

    @ManyToOne
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "hp", nullable = false)
    private Integer hp;

    @Column(name = "dmg", nullable = false)
    private Integer dmg;

    @Column(name = "sun_cost", nullable = false)
    private Integer sunCost;

    @Column(name = "action_speed", nullable = false, length = 50)
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
