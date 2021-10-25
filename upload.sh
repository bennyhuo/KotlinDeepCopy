#!/usr/bin/env bash
clear
./gradlew :reflect-impl:clean :reflect-impl:assemble :reflect-impl:generatePomFileForMavenPublication :reflect-impl:bintrayUpload
./gradlew :annotations:clean :annotations:assemble :annotations:generatePomFileForMavenPublication :annotations:bintrayUpload
./gradlew :apt-impl:compiler:clean :apt-impl:compiler:assemble :apt-impl:compiler:generatePomFileForMavenPublication :apt-impl:compiler:bintrayUpload
./gradlew :runtime:clean :runtime:assemble :runtime:generatePomFileForMavenPublication :runtime:bintrayUpload