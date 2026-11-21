plugins {
  kotlin("jvm") version "2.4.0"
  java
  `java-test-fixtures`
}

group = "daylight"
version = "1.0"

repositories {
  mavenCentral()
}

configurations {
  create("releaseJars")
}

dependencies {
  implementation(fileTree("lib") { include("*.jar") })
  implementation(kotlin("stdlib"))
  testImplementation(kotlin("test"))
  testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
  "releaseJars"(kotlin("stdlib"))
}

sourceSets {
  main {
  }
}

kotlin {
  jvmToolchain(21)
}

java {
  toolchain {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    languageVersion = JavaLanguageVersion.of(21)
  }
}

tasks.test {
  useJUnitPlatform()

  jvmArgs = listOf(
    "-XX:+EnableDynamicAgentLoading",
    "-Xshare:off"
  )
}

tasks.jar {
  manifest {
    attributes["Main-Class"] = "daylight.Daylight"
  }
}
