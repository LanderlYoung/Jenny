package com.young.jenny.gradle

class AptArguments {
    def arguments = []
    def project
    def variant
    def android

    def methodMissing(String name, args) {
        if (args.length == 0) {
            arguments << "-A${name}"
        } else {
            arguments << "-A${name}=${args.join(" ")}"
        }
    }

    def propertyMissing(String name) {
        project[name]
    }
}
