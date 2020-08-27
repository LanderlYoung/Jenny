// Copyright 2019 taylorcyang@tencent.com
// Apache License
// Version 2.0, January 2004
// http://www.apache.org/licenses/

/**
 * <pre>
 * Author: taylorcyang@tencent.com
 * Date:   2019-07-24
 * Time:   20:29
 * Life with Passion, Code with Creativity.
 * </pre>
 */

#pragma once

#include <cassert>
#include <memory>
#include <string>
#include <jni.h>

#ifdef __ANDROID__
#include <pthread.h>
#endif

namespace jenny {

/**
 * persisted jni env scope.
 * attach current thread to jvm, until it's dead.
 */
class Env {
 private:
  JNIEnv* _env;

 public:
  /**
   * MUST be called before any calls.
   * suggested on JNI_OnLoad
   */
  static void attachJvm(JavaVM* jvm);

 public:
  Env() : _env(attachCurrentThreadIfNeed()) {}

  JNIEnv* operator->() const { return _env; }

  JNIEnv* get() const { return _env; }

 private:
  struct StaticState;

  static StaticState* staticState();

  static JNIEnv* attachCurrentThreadIfNeed();
};

template <typename JniPointer>
class GlobalRef;

template <typename JniPointer>
class LocalRef {
 private:
  JniPointer _value;

 public:
  LocalRef(JniPointer value = nullptr) : LocalRef(Env().get(), value) {}

  LocalRef(JNIEnv* env, JniPointer value = nullptr) : _value(value) {
    assert(value == nullptr || env->GetObjectRefType(value) == jobjectRefType::JNILocalRefType);
  }

  LocalRef(const LocalRef&) = delete;
  LocalRef& operator=(const LocalRef&) = delete;

  LocalRef(LocalRef&& from) noexcept : _value(from._value) { from._value = nullptr; }

  LocalRef& operator=(LocalRef&& from) noexcept {
    if (&from != this) {
      if (_value) {
        Env()->DeleteLocalRef(_value);
      }

      _value = from._value;
      from._value = nullptr;
    }
    return *this;
  }

  ~LocalRef() {
    if (_value) {
      Env()->DeleteLocalRef(_value);
    }
  }

  GlobalRef<JniPointer> toGlobal();

  JniPointer get() { return _value; }

  /**
   * same as unique_ptr::release, return the global ref out, and give of the ownership.
   */
  JniPointer release() {
    auto ret = _value;
    _value = nullptr;
    return ret;
  }

  operator bool() { return _value != nullptr; }
};

template <typename JniPointer>
class GlobalRef {
 private:
  JniPointer _value = nullptr;

 public:
  explicit GlobalRef(JniPointer value = nullptr) : GlobalRef(Env().get(), value) {}

  explicit GlobalRef(JNIEnv* env, JniPointer value = nullptr) {
    if (value) {
      _value = env->NewGlobalRef(value);
    }
  }

  GlobalRef(const GlobalRef& from) noexcept : GlobalRef(from._value) {}

  GlobalRef(GlobalRef&& from) noexcept : _value(from._value) { from._value = nullptr; }

  GlobalRef& operator=(const GlobalRef& from) noexcept {
    if (&from != this) {
      clear();

      Env env;
      _value = env->NewGlobalRef(from._value);
    }
  }

  GlobalRef& operator=(GlobalRef&& from) noexcept {
    if (&from != this) {
      clear();
      _value = from._value;
      from._value = nullptr;
    }
    return *this;
  }

  LocalRef<JniPointer> toLocal() const {
    if (_value) {
      Env env;
      return LocalRef<JniPointer>(env->NewLocalRef(_value));
    } else {
      return {};
    }
  }

  ~GlobalRef() { clear(); }

  void clear() {
    if (_value) {
      Env env;
      env->DeleteGlobalRef(_value);
      _value = nullptr;
    }
  }

  JniPointer get() const { return _value; }

  /**
   * same as unique_ptr::release, return the global ref out, and give of the ownership.
   */
  JniPointer release() {
    auto ret = _value;
    _value = nullptr;
    return ret;
  }

  JniPointer operator*() { return _value; }

  operator bool() { return _value != nullptr; }
};

inline bool checkUtfBytes(const char* bytes) {
  while (*bytes != '\0') {
    const uint8_t* utf8 = reinterpret_cast<const uint8_t*>(bytes++);
    // Switch on the high four bits.
    switch (*utf8 >> 4) {
      case 0x00:
      case 0x01:
      case 0x02:
      case 0x03:
      case 0x04:
      case 0x05:
      case 0x06:
      case 0x07:
        // Bit pattern 0xxx. No need for any extra bytes.
        break;
      case 0x08:
      case 0x09:
      case 0x0a:
      case 0x0b:
        // Bit patterns 10xx, which are illegal start bytes.
        return false;
      case 0x0f:
        // Bit pattern 1111, which might be the start of a 4 byte sequence.
        if ((*utf8 & 0x08) == 0) {
          // Bit pattern 1111 0xxx, which is the start of a 4 byte sequence.
          // We consume one continuation byte here, and fall through to consume two more.
          utf8 = reinterpret_cast<const uint8_t*>(bytes++);
          if ((*utf8 & 0xc0) != 0x80) {
            return false;
          }
        } else {
          return false;
        }
        // Fall through to the cases below to consume two more continuation bytes.
      case 0x0e:
        // Bit pattern 1110, so there are two additional bytes.
        utf8 = reinterpret_cast<const uint8_t*>(bytes++);
        if ((*utf8 & 0xc0) != 0x80) {
          return false;
        }
        // Fall through to consume one more continuation byte.
      case 0x0c:
      case 0x0d:
        // Bit pattern 110x, so there is one additional byte.
        utf8 = reinterpret_cast<const uint8_t*>(bytes++);
        if ((*utf8 & 0xc0) != 0x80) {
          return false;
        }
        break;
    }
  }
  return true;
}

template <typename JniPointer>
GlobalRef<JniPointer> LocalRef<JniPointer>::toGlobal() {
  return GlobalRef<JniPointer>(_value);
}

inline LocalRef<jstring> toJavaString(JNIEnv* env, const char* rawString) {
  if (!checkUtfBytes(rawString)) {
    // return null on failure
    return {};
  }
  return LocalRef<jstring>(env->NewStringUTF(rawString));
}

inline LocalRef<jstring> toJavaString(const char* rawString) {
  return toJavaString(Env().get(), rawString);
}

/**
 * this function will do string memcpy. For local variable prefer JStringHolder instead.
 * @param env
 * @param rawString
 * @return
 */
inline std::string fromJavaString(JNIEnv* env, jstring string) {
  if (string == nullptr) {
    return {};
  }
  auto cstr = env->GetStringUTFChars(string, nullptr);
  std::string ret(cstr);
  env->ReleaseStringUTFChars(string, cstr);
  return ret;
}

inline std::string fromJavaString(jstring string) { return fromJavaString(Env().get(), string); }

class StringHolder {
 private:
  JNIEnv* _env;
  jstring _jstr;
  const char* _cStr;
  jsize _length;

 public:
  StringHolder(JNIEnv* env, jstring jstr)
      : _env(env),
        _jstr(jstr),
        _cStr(jstr ? env->GetStringUTFChars(jstr, nullptr) : nullptr),
        _length(jstr ? env->GetStringUTFLength(jstr) : 0) {}

  StringHolder(jstring jstr) : StringHolder(Env().get(), jstr) {}

  StringHolder(const StringHolder&) = delete;
  StringHolder& operator=(const StringHolder&) = delete;

  StringHolder(StringHolder&& move) noexcept
      : _env(move._env), _jstr(move._jstr), _cStr(move._cStr), _length(move._length) {
    move._cStr = nullptr;
    move._jstr = nullptr;
    move._env = nullptr;
    move._length = 0;
  }

  StringHolder& operator=(StringHolder& from) noexcept {
    if (_cStr) {
      _env->ReleaseStringUTFChars(_jstr, _cStr);
    }
    _env = from._env;
    _cStr = from._cStr;
    _jstr = from._jstr;
    _length = from._length;

    from._env = nullptr;
    from._cStr = nullptr;
    from._jstr = nullptr;
    from._length = 0;

    return *this;
  }

  ~StringHolder() {
    if (_cStr) {
      _env->ReleaseStringUTFChars(_jstr, _cStr);
    }
  }

  /**
   * @return return "" instead of nullptr
   */
  const char* c_str() const {
    if (_cStr == nullptr) return "";
    return _cStr;
  }

  const size_t length() const { return static_cast<size_t>(_length); }

  const std::string_view view() const { return std::string_view(c_str(), length()); }
};

class TryCatch {
 private:
  JNIEnv* _env;
  bool _rethrow;

 public:
  explicit TryCatch(JNIEnv* env) : _env(env), _rethrow(false) { assert(!env->ExceptionCheck()); }
  explicit TryCatch() : TryCatch(Env().get()) {}

  TryCatch(const TryCatch&) = delete;
  TryCatch& operator=(const TryCatch&) = delete;

  bool hasCaught() { return _env->ExceptionCheck(); }

  LocalRef<jthrowable> exception() { return LocalRef<jthrowable>(_env->ExceptionOccurred()); }

  bool rethrow() {
    auto e = exception();
    if (e) {
      _rethrow = true;
      _env->Throw(e.get());
    }
    return e;
  }

  ~TryCatch() {
    if (!_rethrow && hasCaught()) {
      _env->ExceptionClear();
    }
  }
};

// impl for Env

#ifdef __ANDROID__

struct Env::StaticState {
  JavaVM* _jvm{};
  ::pthread_key_t envWrapperKey{};

  StaticState() {
    ::pthread_key_create(&envWrapperKey, [](void*) {
      auto state = Env::staticState();
      assert(state->_jvm);
      state->_jvm->DetachCurrentThread();
    });
  }
};

inline Env::StaticState* Env::staticState() {
  static Env::StaticState state{};
  return &state;
}

void Env::attachJvm(JavaVM* jvm) {
  auto state = staticState();
  state->_jvm = jvm;
}

inline JNIEnv* Env::attachCurrentThreadIfNeed() {
  JNIEnv* env;
  auto state = staticState();
  auto jvm = state->_jvm;
  if (jvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) == JNI_EDETACHED) {
    assert(jvm);

    // thread_local on some version of NDK have bug, dtor not called...
    env = static_cast<JNIEnv*>(::pthread_getspecific(state->envWrapperKey));

    if (!env) {
      jvm->AttachCurrentThread(&env, nullptr);
      assert(env);

      ::pthread_setspecific(state->envWrapperKey, env);
    }
  }
  assert(env != nullptr);
  return env;
}

#else

struct Env::StaticState {
  JavaVM* _jvm{};
};

inline Env::StaticState* Env::staticState() {
  static Env::StaticState state{};
  return &state;
}

void Env::attachJvm(JavaVM* jvm) {
  auto state = staticState();
  state->_jvm = jvm;
}

inline JNIEnv* Env::attachCurrentThreadIfNeed() {
  JNIEnv* env;
  auto state = staticState();
  auto jvm = state->_jvm;
  if (jvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) == JNI_EDETACHED) {
    assert(jvm);

    struct EnvWrapper {
      JavaVM* const jvm;
      JNIEnv* env;

      explicit EnvWrapper(JavaVM* _jvm) : jvm(_jvm), env(nullptr) {
        _jvm->AttachCurrentThread(&env, nullptr);
        assert(env != nullptr);
      }

      ~EnvWrapper() {
        // detach when our thread is exiting.
        jvm->DetachCurrentThread();
      }
    };

    thread_local EnvWrapper envWrapper(jvm);
    env = envWrapper.env;
  }
  assert(env != nullptr);
  return env;
}

#endif

}  // namespace jni
