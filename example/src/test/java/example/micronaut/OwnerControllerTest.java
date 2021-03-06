package example.micronaut;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import javax.inject.Inject;
import java.util.Collection;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class OwnerControllerTest {

    @Inject
    OwnerClient ownerClient;

    @Test
    void testGetHealthPets() {
        Collection<Pet> pets = ownerClient.getPets("Barney", Pet.PetHealth.VACCINATED);
        assertEquals(
                1,
                pets.size()
        );
    }

    @Client("/owners")
    interface OwnerClient {

        @Get("/{owner}/pets{?health}")
        Collection<Pet> getPets(String owner, @Nullable Pet.PetHealth health);

        @Get("/{owner}/pets/{pet}")
        Pet getPet(String owner, String pet);
    }
}