package com.isadounikau.phrase.api.client.models.downloads

/*
* Representation of supported file format https://help.phrase.com/help/supported-platforms-and-formats
* */
enum class FileFormat(val apiName: String) {
    JSON("json"),
    JAVA_PROPERTY("properties"),
    ANDROID_XML("xml"),
    IOS_STRINGS("strings"),
    CSV("csv")
}
