    ${return_val} get${camel_case_name}(JNIEnv *env) const {
        return env->Get${static}${_type}Field(${clazz_or_obj}, ${field_id});
    }

    void set${camel_case_name}(JNIEnv *env, ${type} ${name}) const {
        env->Set${static}${_type}Field(${clazz_or_obj}, ${field_id}, ${name});
    }
