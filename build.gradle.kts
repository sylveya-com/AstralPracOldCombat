import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

group = "dev.lokspel"
version = "1.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.withType<ProcessResources>().configureEach {
    filteringCharset = "UTF-8"
    val pluginVersion = project.version.toString()
    inputs.property("version", pluginVersion)

    filesMatching("plugin.yml") {
        expand("version" to pluginVersion)
    }
}

dependencies {
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Paper API
    compileOnly(libs.paper.api)

    // AstralPractice API (JitPack)
    compileOnly(libs.astralpractice.api)

    // OldCombatMechanics
    compileOnly(files("libs/OldCombatMechanics.jar"))

    // Configura & Strata
    implementation(libs.bundles.configura)
    implementation(libs.bundles.strata)

}

tasks.jar {
    archiveClassifier.set("unshaded")
}

tasks.shadowJar {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    mergeServiceFiles()

    exclude("META-INF/maven/**", "META-INF/proguard/**")
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    exclude("**/module-info.class", "org/intellij/**", "org/jetbrains/**", "org/jspecify/**")
    exclude("**/.DS_Store")

    relocate("me.whereareiam.configura", "dev.lokspel.astralpracoldcombat.dependencies.configura")
    relocate("me.whereareiam.strata", "dev.lokspel.astralpracoldcombat.dependencies.strata")
    relocate("com.fasterxml.jackson", "dev.lokspel.astralpracoldcombat.dependencies.jackson")
    relocate("org.yaml.snakeyaml", "dev.lokspel.astralpracoldcombat.dependencies.snakeyaml")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

defaultTasks("build")
