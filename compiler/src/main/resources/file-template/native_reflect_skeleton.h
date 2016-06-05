#pragma once

#include <jni.h>
#include <assert.h>

#ifdef __EXCEPTIONS
#include <stdexcept>
#endif

#define CHECK_NULL(val) do {if ((val) == nullptr) return false;} while(false)

class ${cpp_class_name} {
public:
    static constexpr const char *const FULL_CLASS_NAME = "${full_class_name_const}";
private:
    static jclass sClazz;

${constructors_id_declare}
${methods_id_declare}
${fields_id_declare}
    bool mGlobal;
    jobject mJavaObjectReference;

public:
    static bool init_clazz(JNIEnv *env) {
        if (sClazz == 0) {
            auto localClazz = env->FindClass(FULL_CLASS_NAME);
            CHECK_NULL(localClazz);
            sClazz = reinterpret_cast<jclass>(env->NewGlobalRef(localClazz));
            CHECK_NULL(sClazz);
            ${constructors_id_init};
            ${methods_id_init};
            ${fields_id_init};
            return true;
        }
        return true;
    }

    static void release_clazz(JNIEnv *env) {
        if (sClazz != 0) {
            env->DeleteGlobalRef(sClazz);
            sClazz = 0;
        }
    }

${constructors}

    ///throw std::runtime_error when construct GlobalRef failed
    ${cpp_class_name}(JNIEnv *env, jobject javaObj, bool global)
#ifdef __EXCEPTIONS
    throw(std::runtime_error)
#endif
            : mGlobal(global),
              mJavaObjectReference(global ? env->NewGlobalRef(javaObj) : javaObj) {
#ifdef __EXCEPTIONS
        if (mGlobal && mJavaObjectReference == 0) {
            throw std::runtime_error("cannot create global reference");
        }
#endif
    }

    bool isGlobalJavaReferencePresent() {
        return mJavaObjectReference != 0;
    }

    ///no copy construct
    ${cpp_class_name}(const ${cpp_class_name} &from) = delete;

    void deleteGlobalReference(JNIEnv *env) {
        if (mGlobal) {
            env->DeleteGlobalRef(mJavaObjectReference);
            mJavaObjectReference = 0;
        }
    }

    ~${cpp_class_name}() {
        assert(!mGlobal || mJavaObjectReference == 0);
    }

${methods}

${fields_getter_setter}
};

//static fields
jclass ${cpp_class_name}::sClazz = 0;
${static_declare}

#undef CHECK_NULL
