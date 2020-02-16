import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  freeCompilerArgs = listOf("-Xuse-experimental=kotlin.Experimental", "-Xinline-classes")
}

repositories {
  jcenter()
}

plugins {
  application
  kotlin("jvm") version "1.3.61"
}


dependencies {
  implementation(kotlin("stdlib"))

  testImplementation("org.assertj:assertj-core:3.12.2")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
  testImplementation("org.awaitility:awaitility-kotlin:4.0.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

tasks.test {
  useJUnitPlatform()
}


application {
  mainClassName = "com.annasystems.marketplace.AppKt"
}
