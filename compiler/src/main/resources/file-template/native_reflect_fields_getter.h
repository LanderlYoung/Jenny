    ${_static}${return_type} get${camel_case_name}(JNIEnv *env) ${_const}{
        init_clazz(env);
        return env->Get${static}${_type}Field(${clazz_or_obj}, ${field_id});
    }
