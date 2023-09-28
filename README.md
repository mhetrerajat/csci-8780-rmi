# csci-8780-rmi

Distributed String Array with Concurrency Control Using Java RMI

[Project Specification](./docs/RemoteStringArray-JavaRMI.pdf)

## Usage

### Build the Project

To build the project, execute the following commands:

```bash
gradle clean
gradle build
```

### Run the Application

To run the application, follow these steps in order:

```bash
gradle runRMIServer
gradle runRMIServer
```

## Environment

```
# Java
openjdk 17.0.8.1 2023-08-24
OpenJDK Runtime Environment Homebrew (build 17.0.8.1+0)
OpenJDK 64-Bit Server VM Homebrew (build 17.0.8.1+0, mixed mode, sharing)

# Gradle 

------------------------------------------------------------
Gradle 7.6
------------------------------------------------------------

Build time:   2022-11-25 13:35:10 UTC
Revision:     daece9dbc5b79370cc8e4fd6fe4b2cd400e150a8

Kotlin:       1.7.10
Groovy:       3.0.13
Ant:          Apache Ant(TM) version 1.10.11 compiled on July 10 2021
JVM:          17.0.8.1 (Homebrew 17.0.8.1+0)
OS:           Mac OS X 13.6 x86_64
```

## References

[Gradle Sample: Building Java Applications - Multi-Project](https://docs.gradle.org/7.6/samples/sample_building_java_applications_multi_project.html)
