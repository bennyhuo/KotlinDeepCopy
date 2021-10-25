#!/usr/bin/env bash
clear
./gradlew :reflect-impl:clean :reflect-impl:assemble :reflect-impl:generatePomFileForMavenPublication :reflect-impl:publishMavenPublicationToMavenLocal
./gradlew :annotations:clean :annotations:assemble :annotations:generatePomFileForMavenPublication :annotations:publishMavenPublicationToMavenLocal
./gradlew :apt-impl:compiler:clean :apt-impl:compiler:assemble :apt-impl:compiler:generatePomFileForMavenPublication :apt-impl:compiler:publishMavenPublicationToMavenLocal