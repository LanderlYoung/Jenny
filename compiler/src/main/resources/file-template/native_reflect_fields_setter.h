    ${_static}void set${camel_case_name}(JNIEnv *env, ${type} ${name}) ${_const}{
        init_clazz(env);
        env->Set${static}${_type}Field(${clazz_or_obj}, ${field_id}, ${name});
    }

