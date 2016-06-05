    //construct
    static jobject newInstance(JNIEnv *env${param_declare}) noexcept {
        if (init_clazz(env)) {
            return env->NewObject(sClazz, ${constructor_method_id}${param_val});
        }
        return nullptr;
    }
