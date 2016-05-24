package com.young.util.jni.generator;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Author: LanderlYoung
 * Date:   2014-12-18
 * Time:   10:02
 * Life with passion. Code with creativity!
 */
public final class Environment {
    public final Messager messager;
    public final Types typeUtils;
    public final Elements elementUtils;
    public final Filer filer;
    public final RoundEnvironment roundEnvironment;

    public Environment(Messager messager,
                       Types types,
                       Elements elements,
                       Filer filer,
                       RoundEnvironment environment) {
        this.messager = messager;
        this.typeUtils = types;
        this.elementUtils = elements;
        this.filer = filer;
        this.roundEnvironment = environment;
    }
}
