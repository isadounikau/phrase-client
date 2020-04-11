[![Build Status](https://travis-ci.com/isadounikau/phrase-client.svg?branch=master)](https://travis-ci.com/isadounikau/phrase-client)
[![codecov](https://codecov.io/gh/isadounikau/phrase-client/branch/master/graph/badge.svg)](https://codecov.io/gh/isadounikau/phrase-client)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.isadounikau/phrase-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.isadounikau/phrase-client/badge.svg)
# Phrase.com Api Client with ETag caching support
Java/Kotlin Phrase API client. Library supports **ETag caching**, therefore **you can forget about Rate Limiting** exceptions problem

## What is this?
This projects contain client to handle the API from [PhraseApp API v2](http://docs.phraseapp.com/api/v2/).
It's supposed to expose Phrase Core within the Java/Kotlin world.

## How to use it

You need configure your client 
```
val config =  PhraseApiClientConfig(
    authKey = "authKey"
)

val phraseApiClient = PhraseApiClientImpl(config)
```
## Supported API
* Project
  * Create project
  * Get project by id
  * Get all projects
  * Delete project
  * Update project

* Locale
  * Create locale
  * Get all locales for project
  * Get locale by id
  * Delete locale
  * Download locale translations
    * [JSON](https://help.phrase.com/help/chrome-json-messages)
    * [Java Proerties](https://help.phrase.com/help/java-properties)
  
* Translation
  * Create translation

* Key
  * Create key
  * Search key
  * Delete key

## TODO
* Increase test coverage
* Increase supported APIs
* Migrate from Maven to Gradle 
* *Optional* introduce kotlin-multiplatform 
