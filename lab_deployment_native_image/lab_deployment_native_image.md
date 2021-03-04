# Deploying Micronaut Applications to the Cloud

## Introduction
In this lab you will learn how to deploy your Micronaut application to a VM, a Docker Container Registry, and Kubernetes.

Estimated Lab Time: 20 minutes

### Objectives

In this lab you will:
* Learn how to build an executable JAR file that can be deployed to any VM
* Learn how to build native executable that can be deployed to any VM
* Understand how to build and deploy Docker images to Oracle Container Registry


### Prerequisites

- Access to your project instance

## Deploying an Executable JAR to a VM

### Gradle

To build the application as an executable JAR, open up Terminal (ALT + F12) and run the following:

    <copy>
    ./gradlew assemble
    </copy>

An executable JAR file will be built that is ready to be run in production on a VM.

To execute the JAR file run:

    <copy>
    java -jar build/libs/demo-0.1-all.jar
    </copy>

Once the application is up and running, you can access it via `http://[YOUR IP]:8080/owners`.

> NOTE: Make sure you run the JAR that ends with `-all`!

> NOTE: Remember to use the `http` protocol when accessing the instance on port 8080 and not `https` otherwise you will receive an error.

### Maven

To build the application as an executable JAR open up Terminal (ALT + F12) and run the following command in Maven:

    <copy>
    ./mvnw package -DskipTests
    </copy>

An executable JAR file will be built that is ready to be run in production on a VM.

To execute the JAR file run:

    <copy>
    java -jar target/example-0.1.jar
    </copy>

Once the application is up and running, you can access it via `http://[YOUR IP]:8080/owners`.

> NOTE: Remember to use the `http` protocol when accessing the instance on port 8080 and not `https` otherwise you will receive an error.

## Deploying the Native Image to a VM

The Native Image you built in the previous Lab can also easily be executed directly on a VM.

### Gradle

To deploy the native executable built with Gradle run:

    <copy>
    ./build/native-image/application
    </copy>

Once the application is up and running, you can access it via `http://[YOUR IP]:8080/owners`.
### Maven

To deploy the native executable built with Maven run:

    <copy>
    ./target/example
    </copy>

Once the application is up and running, you can access it via `http://[YOUR IP]:8080/owners`.

> NOTE: Remember to use the `http` protocol when accessing the instance on port 8080 and not `https` otherwise you will receive an error.

## Deploying a Container to Oracle Container Registry

A common way to deploy applications is via Containers and services such as [Oracle Container Engine for Kubernetes](https://www.oracle.com/cloud-native/container-engine-kubernetes/) which allows orchestrating and running these containers.

[Oracle Cloud Infrastructure Registry (OCIR)](https://docs.cloud.oracle.com/en-us/iaas/Content/Registry/Concepts/registryoverview.htm) allows pushing Docker containers to Oracle Cloud that can be used as a public or private Docker registry.

### Gradle

To deploy a container to OCIR via Gradle your `build.gradle` first needs to be configured. Add the following to `build.gradle`:

    <copy>
    dockerBuild {
        images = ["phx.ocir.io/cloudnative-devrel/micronaut-labs/${System.env.DOCKER_USER}/$project.name:$project.version"]
    }

    dockerBuildNative {
        images = ["phx.ocir.io/cloudnative-devrel/micronaut-labs/${System.env.DOCKER_USER}/$project.name:$project.version"]
    }
    </copy>

The first `dockerBuild` definition defines the image to publish for the JVM version of the application whilst the `dockerBuildNative` definition defines the image for the native version.

Images in OCIR are specified in the form `[REGION].ocir.io/[TENANCY]/[REPOSITORY]/[NAME]:[VERSION]` where each part of the image name corresponds the the following:

* `[REGION]` - Your Oracle Cloud Availability [Region](https://docs.cloud.oracle.com/en-us/iaas/Content/Registry/Concepts/registryprerequisites.htm#Availab)
* `[TENANCY]` - Your Oracle Cloud Tenancy
* `[REPOSITORY]` - A unique repository name (try using your name and surname)
* `[NAME]` - The name of the image
* `[VERSION]` - The version of the image

The example above will publish to the Phoenix region (`phx`) using the Tenancy `cloudnative-devrel`, a repository name prefixed with `micronaut-labs/` and followed by the name of the current Docker user which is exposed in an environment variable called `DOCKER_USER` for this example.

Now to publish a Docker Container for the JVM version use:

    <copy>
    ./gradlew dockerPush
    </copy>

And to publish the GraalVM native version use:

    <copy>
    ./gradlew dockerPushNative
    </copy>

### Maven

To Deploy a container to OCIR via Maven your `pom.xml` first needs to be configured. Add the following plugin under the `<plugins>` section in `pom.xml`:

    <copy>
      <plugin>
        <groupId>com.google.cloud.tools</groupId>
        <artifactId>jib-maven-plugin</artifactId>
        <configuration>
          <to>
            <image>phx.ocir.io/cloudnative-devrel/micronaut-labs/${env.DOCKER_USER}/${project.artifactId}:${project.version}</image>
          </to>
        </configuration>
      </plugin>
    </copy>

Images in OCIR are specified in the form `[REGION].ocir.io/[TENANCY]/[REPOSITORY]/[NAME]:[VERSION]` where each part of the image name corresponds the the following:

* `[REGION]` - Your Oracle Cloud Availability [Region](https://docs.cloud.oracle.com/en-us/iaas/Content/Registry/Concepts/registryprerequisites.htm#Availab)
* `[TENANCY]` - Your Oracle Cloud Tenancy
* `[REPOSITORY]` - A unique repository name (try using your name and surname)
* `[NAME]` - The name of the image
* `[VERSION]` - The version of the image

The example above is publishing to the Phoenix region (`phx`) using the Tenancy `cloudnative-devrel`, a repository name prefixed with `micronaut-labs/` and followed by the name of the current Docker user which is exposed in an environment variable called `DOCKER_USER` for this example.

Now to publish a Docker Container for the JVM version use:

    <copy>
    ./mvnw deploy -Dpackaging=docker
    </copy>

And to publish the native version use the `docker-native` packaging instead:

    <copy>
    ./mvnw deploy -Dpackaging=docker-native
    </copy>

Congratulations, you have completed the workshop and deployed your Micronaut application to Oracle Cloud!

For further reading see the following links:

- [Micronaut](https://micronaut.io/)
- [GraalVM](https://www.graalvm.org/)

### Acknowledgements
- **Instructors** - Ali Parvini, Amitpal Dhillon, Munish Chouhan
- **Owners** - Graeme Rocher, Architect, Oracle Labs - Databases and Optimization
- **Contributors** - Palo Gressa, Todd Sharp, Eric Sedlar
