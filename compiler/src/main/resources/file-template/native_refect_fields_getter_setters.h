    ${return_val} get${camel_case_name}(JNIEnv *env) {
        return env->Get${static}${_type}Field(${clazz_or_obj}, ${field_id});
    }

    void set${camel_case_name}(JNIEnv *env, ${type} ${name}) {
        env->Set${static}${_type}Field(${clazz_or_obj}, ${field_id}, ${name});
    }