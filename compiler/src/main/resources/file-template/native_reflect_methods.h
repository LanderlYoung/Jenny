    ${static_modifier}${return_value} ${name}(JNIEnv *env${param_declare}) {
        ${return}env->Call${static}${type}Method(${clazz_or_obj}, ${method_id}${param_value});
    }

