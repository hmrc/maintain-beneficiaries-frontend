#!/usr/bin/env bash

sbt clean scalastyleAll compile coverage test it/Test/test coverageOff coverageReport dependencyUpdates
