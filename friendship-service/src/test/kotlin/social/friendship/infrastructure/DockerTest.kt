package social.friendship.infrastructure

import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

abstract class DockerTest {
    private val logger = LogManager.getLogger(this::class)

    /**
     * Copies a file from the resources folder to a temporary folder.
     *
     * @return the copied file
     */
    internal fun copyFileToTempFolder(path: String): File {
        val resource = this::class.java.classLoader.getResource(path) ?: error("Resource not found")
        val sourceFile = File(resource.toURI())
        val fileName = sourceFile.nameWithoutExtension
        val fileExtension = sourceFile.extension
        val tempFile = File.createTempFile(fileName, ".$fileExtension")
        Files.copy(sourceFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        return tempFile
    }

    /**
     * Creates a docker-compose file from a resource file, replacing the variables in the file with the values provided
     * in the assignments parameter.
     *
     * @return the generated docker-compose file
     */
    internal fun <T> createDockerComposeFile(klass: Class<T>, resourcePath: String, path: File, vararg assignments: Pair<String, String>): File {
        val variablesAssignments = assignments.toMap()
        val resource = klass.classLoader.getResource(resourcePath) ?: error("docker-compose.yaml resource not found")

        resource.openStream()?.bufferedReader()?.use { reader ->
            path.bufferedWriter().use { writer ->
                reader.forEachLine { line ->
                    val newLine = variablesAssignments.entries.fold(line) { acc, (variable, value) ->
                        acc.replace("__${variable}__", value)
                    }
                    writer.write(newLine)
                    writer.newLine()
                }
            }
        }
        return path
    }

    internal fun executeDockerComposeCmd(composeFile: File, vararg arguments: String) {
        if (!composeFile.exists()) {
            throw IllegalStateException("File not found: ${composeFile.absolutePath}")
        }

        val command = mutableListOf("docker", "compose", "-f", "\"${composeFile.absolutePath}\"")
        command.addAll(arguments)
        val processBuilder = ProcessBuilder()
            .command(command)
            .redirectErrorStream(true)
            .directory(composeFile.parentFile)

        val process = processBuilder.start()
        val output = process.inputStream.bufferedReader().use { it.readText() } // Capture output
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            logger.error("error when starting docker-compose: $output")
            throw RuntimeException("Failed to start docker-compose: Exit code $exitCode")
        }
    }
}
