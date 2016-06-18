    ${_static}${return_type} get${camel_case_name}(JNIEnv *env) ${_const}{
        init_clazz(env);
        return ${return_statement};
    }
