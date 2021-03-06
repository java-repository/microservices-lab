package example.micronaut;

import io.micronaut.context.annotation.EachProperty;
import java.util.Collections;
import java.util.List;

@EachProperty("owners")
public class OwnerConfiguration {

    private String name;
    private int age;
    private List<String> pets = Collections.emptyList();

    public List<String> getPets() {
        return pets;
    }

    public void setPets(List<String> pets) {
        this.pets = pets;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    Owner create() {
        return new Owner(name, age);
    }
}