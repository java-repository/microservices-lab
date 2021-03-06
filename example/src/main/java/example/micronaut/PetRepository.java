package example.micronaut;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Collection;

@Repository
public interface PetRepository extends CrudRepository<Pet, Long> {
    @Join("owner")
    Collection<Pet> findByOwnerName(String owner);

    @Join("owner")
    Pet findByNameAndOwnerName(String pet, String owner);

    @Join("owner")
    Collection<Pet> findByOwnerNameAndHealth(String owner, Pet.PetHealth health);
}