package example.micronaut;

import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerServiceTest {

    @Test
    void testOwnerService() {
        try (ApplicationContext context = ApplicationContext.run()) {
            OwnerService ownerService = context.getBean(OwnerService.class);
            assertSame(
                    ownerService,
                    context.getBean(OwnerService.class)
            );
        }
    }
    @Test
    void testOwners() {
        Map<String, Object> configuration = Map.of(
                "owners.fred.name", "Fred",
                "owners.fred.age", "35",
                "owners.barney.name", "Barney",
                "owners.barney.age", "30"
        );
        try (ApplicationContext context = ApplicationContext.run(configuration)) {
            OwnerService ownerService = context.getBean(OwnerService.class);
            Collection<Owner> initialOwners = ownerService.getInitialOwners();
            assertEquals(
                    2,
                    initialOwners.size()
            );

            assertTrue(
                    initialOwners.stream().anyMatch(o -> o.getName().equals("Fred"))
            );
        }
    }
}