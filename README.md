# Maintain beneficiaries frontend

This service is responsible for updating the information held about a beneficiary in a trust registration.

To run locally using the micro-service provided by the service manager:

***sm2 --start TRUSTS_ALL -r***

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 9793 but is defaulted to that in build.sbt).

`sbt run`

## Testing the service

This service uses [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) to
provide test coverage reports.

Use the following commands to run the tests with coverage and generate a report.

Run this script before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests and integration tests with scalastyle and checks for dependency updates:
```
./run_all_tests.sh
```

Unit tests only:
```
sbt clean coverage test coverageReport
```

Integration tests only:
```
sbt clean coverage it:test coverageReport
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
