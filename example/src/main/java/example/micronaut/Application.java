package example.micronaut;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.micronaut.core.annotation.TypeHint;

@OpenAPIDefinition(
        info = @Info(
                title = "pets-api",
                version = "0.2"
        )
)
@TypeHint(Pet.PetHealth.class)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
