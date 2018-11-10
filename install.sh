#!/usr/bin/env bash
clear
./gradlew :reflect-impl:clean :reflect-impl:assemble :reflect-impl:generatePomFileForMavenPublication :reflect-impl:publishMavenPublicationToMavenLocal
./gradlew :apt-impl:annotations:clean :apt-impl:annotations:assemble :apt-impl:annotations:generatePomFileForMavenPublication :apt-impl:annotations:publishMavenPublicationToMavenLocal
./gradlew :apt-impl:compiler:clean :apt-impl:compiler:assemble :apt-impl:compiler:generatePomFileForMavenPublication :apt-impl:compiler:publishMavenPublicationToMavenLocal