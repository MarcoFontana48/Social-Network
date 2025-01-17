package social.friendship.infrastructure

import org.apache.logging.log4j.LogManager
import java.io.File

abstract class DockerTest {
    private val logger = LogManager.getLogger(this::class)

    internal fun executeDockerComposeCmd(composeFile: File, vararg arguments: String) {
        if (!composeFile.exists()) {
            throw IllegalStateException("File not found: ${composeFile.absolutePath}")
        }

        val command = mutableListOf("docker", "compose", "-f", composeFile.absolutePath)
        command.addAll(arguments)

        val processBuilder = ProcessBuilder()
            .command(command)
            .redirectErrorStream(true)
            .directory(composeFile.parentFile)

        val process = processBuilder.start()
        val output = process.inputStream.bufferedReader().use { it.readText() } // Capture output

        val exitCode = process.waitFor()

        if (exitCode != 0) {
            logger.error("Error when starting docker-compose: $output")
            throw RuntimeException("Failed to start docker-compose: Exit code $exitCode, output: $output")
        }
    }
}
