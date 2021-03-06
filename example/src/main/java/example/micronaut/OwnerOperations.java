package example.micronaut;

import java.util.Collection;

public interface OwnerOperations {

    Collection<Owner> getInitialOwners();

    void addOwner(Owner owner);
    // lookup by owner and pet name
    Pet getPet(String owner, String pet);

    // lookup all by owner
    Collection<Pet> getPets(String owner);

    // lookup all by owner and health
    Collection<Pet> getPetsWithHeath(String owner, Pet.PetHealth health);
}