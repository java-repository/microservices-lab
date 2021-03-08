# Writing Web Controllers with Micronaut

## Introduction
In this lab you will learn how to write a REST API that produces JSON output.

Estimated Lab Time: 25 minutes

### Objectives

In this lab you will:
* Learn how to define Beans
* Understand how to inject one bean to another
* Learn the benefits of loose coupling
* How to write POJOs that define your JSON responses
* How to write controller routes that match requests
* How to return responses from the controller

### Prerequisites
- Access to your project instance

## Defining Managed Beans

Micronaut is fundamentally based on Dependency Injection (DI) techniques that have been used in Java applications for many years in frameworks like Spring and CDI.

The primary difference is that Micronaut will at compilation time compute the injection rules necessary to wire together your application.

In order for Micronaut to do that you need to designate which classes in your application are managed "beans".

This is done by defining an annotation on the class that itself is annotated with `javax.inject.Scope`.

A scope defines the lifecycle of a bean and aspects such as how many instances are allowed. The most common scope is `javax.inject.Singleton` which indicates that at most 1 instance is allowed of the object.

For more information on other available scopes see the [Scopes](https://docs.micronaut.io/latest/guide/index.html#scopes) section of the Micronaut documentation.

Try creating a bean in a file called `src/main/java/example/micronaut/OwnerService.java`:

    <copy>
    package example.micronaut;

    import javax.inject.Singleton;

    @Singleton
    public class OwnerService {

    }
    </copy>

The `OwnerService` class is annotated with `@Singleton` which means it is now managed by Micronaut and available as a bean to be injected into other objects.

To demonstrate this define a test in `src/test/java/example/micronaut/OwnerServiceTest.java`:

    <copy>
    package example.micronaut;

    import io.micronaut.context.ApplicationContext;
    import org.junit.jupiter.api.Test;
    import static org.junit.jupiter.api.Assertions.*;

    public class OwnerServiceTest {

        @Test
        void testOwnerService() {
            try (ApplicationContext context = ApplicationContext.run()) {
                OwnerService ownerService = context.getBean(OwnerService.class);
                assertNotNull(ownerService);
            }
        }
    }
    </copy>

Here you can see the test uses Micronaut's `ApplicationContext`, which is a container object that manages all beans, to lookup an instance of `OwnerService`. Whilst this example is not particularly interesting, if you invoke `getBean` multiple times you will see that the instances are the same:

    <copy>
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
    </copy>

The `@Singleton` scope ensures that only one instance is created.

## Injecting Beans

To demonstrate dependency injection better, let's tackle a more interesting case. First define a POJO class to represent the owners of a pet in a hypothetical pet clinic in a file called `src/main/java/example/micronaut/Owner.java`:

    <copy>
    package example.micronaut;

    public class Owner {
        private final String name;
        private final int age;

        public Owner(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
    </copy>

Now let's setup some logic to configure an initial set of owners using configuration injection to resolve values from the environment in a file called `src/main/java/example/micronaut/OwnerConfiguration.java`:

    <copy>
    package example.micronaut;

    import io.micronaut.context.annotation.EachProperty;

    @EachProperty("owners")
    public class OwnerConfiguration {
        private String name;
        private int age;

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
    </copy>

This class uses `@EachProperty` which indicates for every property passed to Micronaut that starts with "owners" a new bean of type `OwnerConfiguration` will be created.

With that in place you can inject all of the `OwnerConfiguration` instances into the constructor of the `OwnerService`:

    <copy>
    package example.micronaut;

    import javax.inject.Singleton;
    import java.util.List;
    import java.util.Collection;
    import java.util.stream.Collectors;

    @Singleton
    public class OwnerService {
        private final List<OwnerConfiguration> ownerConfigurations;

        public OwnerService(List<OwnerConfiguration> ownerConfigurations) {
            this.ownerConfigurations = ownerConfigurations;
        }

        public Collection<Owner> getInitialOwners() {
            return ownerConfigurations.stream()
                     .map(OwnerConfiguration::create)
                    .collect(Collectors.toList());
        }
    }
    </copy>

Micronaut will automatically lookup and populate the available `OwnerConfiguration` instances using constructor injection. You can also alternatively use field injection in the form:

    <copy>
    @javax.inject.Inject List<OwnerConfiguration> ownerConfigurations;
    </copy>

However, constructor injection is prefered as it encourages immutability and more clearly expresses the requirements of the class.

So how do you make the `OwnerConfiguration` instances available? Try adding the following test to the `OwnerServiceTest` you created earlier:

    <copy>
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
    </copy>

You'll also need these imports:

    <copy>
    import java.util.Collection;
    import java.util.Map;
    </copy>

Notice that for each entry under the `owner` configuration namespace you get a new instance of `OwnerConfiguration` thanks to how `@EachProperty` works. Also notice how you can pass configuration to the `run` method of the `ApplicationContext` in order to configure your application.

## Define a Controller

Micronaut's built-in HTTP server is based on the Netty I/O toolkit in combination with an annotation-based programming model for defining routes with support for emitting and consuming JSON via the Jackson library.

> Note that Micronaut does include optional additional modules that add support for things like [Server-side view rendering with template engines like Thymleaf and Velocity](https://micronaut-projects.github.io/micronaut-views/latest/guide/) and [XML responses](https://micronaut-projects.github.io/micronaut-jackson-xml/latest/guide/index.html).

The annotation-based programming model should be very familiar to anyone who has used libraries like Spring MVC or JAX-RS (note if you prefer those annotation models it is possible to use [JAX-RS annotations](https://micronaut-projects.github.io/micronaut-jaxrs/latest/guide/index.html) and [Spring annotations](https://micronaut-projects.github.io/micronaut-spring/latest/guide/) with Micronaut as well).

To get started with an example create a file called `src/main/java/example/micronaut/OwnerController.java` and populate it with the following contents:

    <copy>
    package example.micronaut;

    import io.micronaut.http.annotation.Controller;

    @Controller("/owners")
    public class OwnerController {
        private final OwnerService ownerService;

        public OwnerController(OwnerService ownerService) {
            this.ownerService = ownerService;
        }
    }
    </copy>

As you can see the `OwnerController` defines a constructor that injects the `OwnerService` and the class is annotated with `@Controller` to define the root URI for this controller.

## Specify Routes

To expose an individual route over HTTP you need to define methods annotated with an applicable annotation for each HTTP method you wish to expose. Try adding the following definition:

    <copy>
    @Get("/")
    Collection<Owner> getOwners() {
        return ownerService.getInitialOwners();
    }
    </copy>

You'll also need these imports:

    <copy>
    import io.micronaut.http.annotation.Get;
    import java.util.Collection;
    </copy>

This uses the `io.micronaut.http.annotation.Get` annotation to indicate that HTTP `GET` requests to the root URI under `/owners` should match this method and invoke it. The return type represents the response that will be sent over HTTP which by default is assumed to be JSON.

## Return JSON Responses

As mentioned the default response content type is JSON. However to allow many tasks to be peformed without the use of reflection on a Java object such as JSON serialization/deserialization, validation and so on, Micronaut needs access what is known as a [Bean Introspection](https://docs.micronaut.io/latest/guide/index.html#introspection) which allows reading and writing to Java objects according to the rules defined in the [JavaBean specification](https://www.oracle.com/java/technologies/javase/javabeans-spec.html).

To create a Bean Introspection you can annotate any classes required with [@Introspected](https://docs.micronaut.io/latest/api/io/micronaut/core/annotation/Introspected.html). Modify the `Owner` class you created earlier and add the annotation:

    <copy>
    package example.micronaut;

    import io.micronaut.core.annotation.Introspected;

    @Introspected // <-- add the annotation here
    public class Owner {
        // remaining code
    }
    </copy>

Now let's expose some `Owner` objects over HTTP. To see that in action configure some initial owners by modifying your `src/main/resources/application.yml` file so that two `OwnerConfiguration` beans are created:

    <copy>
    micronaut:
      application:
        name: demo
    owners:
      fred:
        name: Fred
        age: 35
      barney:
        name: Barney
        age: 30
    </copy>

Now run your `Application` class from the IDE (as described in Lab 1) and open up Terminal from the IDE and run `curl` to see your response:

```
curl -i http://localhost:8080/owners
HTTP/1.1 200 OK
Date: Thu, 26 Nov 2020 08:27:30 GMT
Content-Type: application/json
content-length: 53
connection: keep-alive

[{"name":"Barney","age":30},{"name":"Fred","age":35}]
```

You may now *proceed to the next lab*.

### Acknowledgements
- **Instructors** - Ali Parvini, Amitpal Dhillon, Munish Chouhan
- **Owners** - Graeme Rocher, Architect, Oracle Labs - Databases and Optimization
- **Contributors** - Palo Gressa, Todd Sharp, Eric Sedlar
