@echo off
sbt\bin\sbt "project specs" "~test-only %1"