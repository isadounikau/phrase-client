[![Build Status](https://travis-ci.com/isadounikau/phrase-client.svg?branch=master)](https://travis-ci.com/isadounikau/phrase-client)
[![codecov](https://codecov.io/gh/isaodunikau/phrase-client/branch/master/graph/badge.svg)](https://codecov.io/gh/isadounikau/phrase-client)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.isadounikau/phrase-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.isadounikau/phrase-client/badge.svg)
# Phrase.com Api Client
Java/Kotlin Phrase API client

## What is this?
This projects contain client to handle the API from [PhraseApp API v2](http://docs.phraseapp.com/api/v2/).
It's supposed to expose Phrase Core within the kotlin world.

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
  
* Translation
  * Create translation

* Key
  * Create key
  * Search key
  * Delete key

