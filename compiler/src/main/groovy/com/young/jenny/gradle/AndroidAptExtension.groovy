package com.young.jenny.gradle

class AndroidAptExtension {
    final def aptArguments = new AptArguments()
    private def argsClosure;

    def arguments(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = aptArguments;
        argsClosure = closure;
    }

    def arguments() {
        aptArguments.arguments.clear();
        if (argsClosure) {
            argsClosure()
        }
        return aptArguments.arguments
    }
}
