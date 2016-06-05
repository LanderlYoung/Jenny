    static ${return_value} ${name}(JNIEnv *env${param_declare}) {
        init_clazz(env);
        ${return}env->Call${static}${type}Method(${clazz_or_obj}, ${method_id}${param_value});
    }

