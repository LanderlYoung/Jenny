            ${name} = env->GetMethodID(sClazz, ${method_name}, "${method_signature}");
            CHECK_NULL(${name});

                        ${name} = env->GetFieldID(sClazz, "${field_name}", "${field_signature}");
                        CHECK_NULL(${name});