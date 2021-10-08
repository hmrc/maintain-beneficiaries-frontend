# Maintain beneficiaries frontend

This service is responsible for updating the information held about a beneficiary in a trust registration.

To run locally using the micro-service provided by the service manager:

***sm --start TRUSTS_ALL -r***

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 9793 but is defaulted to that in build.sbt).

`sbt run`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
