import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Delete

plugins {
    // Apply the java plugin to add support for Java
    java

    // Apply the application plugin to add support for building a CLI application
    // You can run your app via task "run": ./gradlew run
    application

    /*
     * Adds tasks to export a runnable jar.
     * In order to create it, launch the "shadowJar" task.
     * The runnable jar will be found in build/libs/projectname-all.jar
     */
    id("com.gradleup.shadow") version "9.2.2"
    id("org.danilopianini.gradle-java-qa") version "1.157.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

repositories { // Where to search for dependencies
    mavenCentral()
}

dependencies {
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.8")

    /*
     * Apache Log4j2 - Direct logging framework (no SLF4J)
     * See: https://logging.apache.org/log4j/2.x/
     */
    val log4j2Version = "2.25.2"
    implementation("org.apache.logging.log4j:log4j-api:$log4j2Version")
    implementation("org.apache.logging.log4j:log4j-core:$log4j2Version")

    // JUnit API and testing engine
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.tetraj.Main")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform() // Enables the engine of JUnit 5/6
    testLogging { // Additional Options
        // Display all events (test started, succeeded, failed...)
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.entries.toTypedArray())
        showStandardStreams = true // Show the standard output
    }
}

// Build LaTeX report
val latexSourceDir = "report"
val latexMainFile = "main.tex"

fun latexTask(name: String, args: String) =
    tasks.register<Exec>(name) {
        group = "documentation"
        description = "LaTeX task: $name"

        onlyIf {
            try {
                val p = ProcessBuilder("docker", "--version")
                    .redirectErrorStream(true)
                    .start()

                p.waitFor() == 0
            } catch (e: Exception) {
                println("⚠️ Docker is not available, skipping LaTeX build")
                false
            }
        }

        workingDir = projectDir

        inputs.dir(project.layout.projectDirectory.dir(latexSourceDir))

        commandLine = listOf(
            "docker", "run", "--rm",
            "-v", "${projectDir.resolve(latexSourceDir)}:/source:ro",
            "-v", "${projectDir}:/build",
            "-w", "/source",
            "texlive/texlive:latest",
            "sh", "-c",
            "latexmk -r /source/.latexmkrc $args"
        )
    }

latexTask("buildPdf", latexMainFile)
latexTask("cleanPdf", "-c $latexMainFile")

tasks.clean {
    dependsOn("cleanPdf")
}