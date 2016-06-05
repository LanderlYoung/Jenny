    static ${return_val} get${camel_case_name}(JNIEnv *env) {
        init_clazz(env);
        return env->Get${static}${_type}Field(${clazz_or_obj}, ${field_id});
    }

    static void set${camel_case_name}(JNIEnv *env, ${type} ${name}) {
        init_clazz(env);
        env->Set${static}${_type}Field(${clazz_or_obj}, ${field_id}, ${name});
    }
