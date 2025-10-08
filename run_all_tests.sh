#!/usr/bin/env bash

sbt clean compile coverage test it/Test/test coverageOff coverageReport dependencyUpdates
