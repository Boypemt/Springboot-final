package th.mfu.pvz.catalog.domain;

import javax.persistence.*;

@Entity
@Table(name = "classes")
public class PlantClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String classname;

    private String description;

    public PlantClass() {
    }

    public PlantClass(Long id, String classname, String description) {
        this.id = id;
        this.classname = classname;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
