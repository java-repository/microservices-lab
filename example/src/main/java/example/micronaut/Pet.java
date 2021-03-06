package example.micronaut;

import edu.umd.cs.findbugs.annotations.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Owner owner;

    private PetHealth health = PetHealth.VACCINATED;

    public Pet() {
    }

    public Pet(String name, @NonNull Owner owner) {
        this.name = name;
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PetHealth getHealth() {
        return health;
    }

    public void setHealth(PetHealth health) {
        this.health = health;
    }

    public enum PetHealth {
        VACCINATED,
        REQUIRES_VACCINATION
    }
}