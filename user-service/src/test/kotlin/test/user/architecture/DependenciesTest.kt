package test.user.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

internal class DependenciesTest {
    @Test
    fun layerDependenciesAreRespected() {
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Infrastructure").definedBy("social.user.infrastructure..")
            .layer("Application").definedBy("social.user.application..")
            .layer("Domain").definedBy("social.user.domain..")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Domain").mayNotAccessAnyLayer()
            .check(ClassFileImporter().importPackages("social.user..."))
    }
}
