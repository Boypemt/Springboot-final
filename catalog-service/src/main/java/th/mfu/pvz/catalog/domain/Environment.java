package th.mfu.pvz.catalog.domain;

import javax.persistence.*;

@Entity
@Table(name = "environments")
public class Environment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String envname;

    private String description;

    public Environment() {
    }

    public Environment(Long id, String envname, String description) {
        this.id = id;
        this.envname = envname;
        this.description = description;
    }

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
