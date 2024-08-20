// Copyright 2019 taylorcyang@tencent.com
// Apache License
// Version 2.0, January 2004
// http://www.apache.org/licenses/
//
// This file is part of the Jenny project.
// https://github.com/LanderlYoung/Jenny

/**
 * <pre>
 * Author: landerlyoung@gmail.com
 * Date:   2019-07-24
 * Time:   20:29
 * Life with Passion, Code with Creativity.
 * </pre>
 */

#pragma once

#include <jni.h>
#include <cassert>  // assert
#include <cstring>  // memcpy, memset
#include <utility>  // swap, move
#include <string>
#if defined(__has_include) && __has_include(<string_view>)
#include <string_view>
#endif

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

  static void attachJvm(JNIEnv* env) {
    JavaVM* jvm;
    env->GetJavaVM(&jvm);
    attachJvm(jvm);
  }

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
  JNIEnv* _env;
  bool _owned;

 public:
  LocalRef() : LocalRef(nullptr, nullptr, false) {}

  explicit LocalRef(JniPointer value, bool owned = true) : LocalRef(Env().get(), value, owned) {}

  LocalRef(JNIEnv* env, JniPointer value = nullptr, bool owned = true)
      : _value(value), _env(env), _owned(owned) {
    assert(value == nullptr || env->GetObjectRefType(value) == jobjectRefType::JNILocalRefType);
  }

  LocalRef(const LocalRef& copy) : _value(nullptr), _env(nullptr), _owned(true) {
    if (copy._value) {
      _env = copy._env;
      _value = reinterpret_cast<JniPointer>(_env->NewLocalRef(copy._value));
      _owned = true;
      assert(_value);
    }
  }

  LocalRef(LocalRef&& from) noexcept : _value(from._value), _env(from._env), _owned(from._owned) {
    from._value = nullptr;
    from._env = nullptr;
    from._owned = false;
  }

  LocalRef& operator=(LocalRef other) noexcept {
    swap(other);
    return *this;
  }

  void swap(LocalRef<JniPointer>& other) noexcept {
    std::swap(_value, other._value);
    std::swap(_env, other._env);
    std::swap(_owned, other._owned);
  }

  ~LocalRef() {
    if (_value && _owned) {
      _env->DeleteLocalRef(_value);
      _env = nullptr;
    }
  }

  GlobalRef<JniPointer> toGlobal() const;

  JniPointer get() const { return _value; }

  bool owned() const { return _owned; }

  /**
   * same as unique_ptr::release, return the ref out, and give of the ownership.
   */
  JniPointer release() {
    auto ret = _value;
    _value = nullptr;
    return ret;
  }

  operator bool() const { return _value != nullptr; }
};

template <typename JniPointer>
class GlobalRef {
 private:
  JniPointer _value = nullptr;

 public:
  explicit GlobalRef(JniPointer value = nullptr) : GlobalRef(Env().get(), value) {}

  explicit GlobalRef(const LocalRef<JniPointer>& local) : GlobalRef(local.get()) {}

  explicit GlobalRef(JNIEnv* env, JniPointer value = nullptr) {
    if (value) {
      _value = reinterpret_cast<JniPointer>(env->NewGlobalRef(value));
    }
  }

  GlobalRef(const GlobalRef& from) noexcept : GlobalRef(from._value) {}

  GlobalRef(GlobalRef&& from) noexcept : _value(from._value) { from._value = nullptr; }

  GlobalRef& operator=(GlobalRef from) noexcept {
    swap(from);
    return *this;
  }

  void swap(GlobalRef& other) noexcept { std::swap(_value, other._value); }

  LocalRef<JniPointer> toLocal() const {
    if (_value) {
      return LocalRef<JniPointer>(reinterpret_cast<JniPointer>(Env()->NewLocalRef(_value)));
    } else {
      return {};
    }
  }

  ~GlobalRef() { clear(); }

  void clear() {
    if (_value) {
      Env()->DeleteGlobalRef(_value);
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

  operator bool() const { return _value != nullptr; }
};

inline bool checkUtfBytes(const char* bytes) {
  if (bytes == nullptr) return false;
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
GlobalRef<JniPointer> LocalRef<JniPointer>::toGlobal() const {
  return GlobalRef<JniPointer>(_env, _value);
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

inline std::string fromJavaString(const LocalRef<jstring>& string) {
  return fromJavaString(Env().get(), string.get());
}

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

  StringHolder(const LocalRef<jstring>& jstr) : StringHolder(Env().get(), jstr.get()) {}

  StringHolder(const StringHolder&) = delete;
  StringHolder& operator=(const StringHolder&) = delete;

  StringHolder(StringHolder&& move) noexcept
      : _env(move._env), _jstr(move._jstr), _cStr(move._cStr), _length(move._length) {
    move._cStr = nullptr;
    move._jstr = nullptr;
    move._env = nullptr;
    move._length = 0;
  }

  StringHolder& operator=(StringHolder&& from) noexcept {
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

#ifdef __cpp_lib_string_view
  const std::string_view view() const { return std::string_view(c_str(), length()); }
#endif
};

class ByteArrayHolder {
  jenny::Env env_;
  jbyteArray array_;

  jbyte* data_ = nullptr;
  size_t length_ = 0;
  bool commit_ = false;

 public:
  ByteArrayHolder(const jenny::LocalRef<jbyteArray>& array) : ByteArrayHolder(array.get()) {}

  ByteArrayHolder(jbyteArray array) : env_(), array_(array) {
    if (array) {
      jboolean isCopy;
      data_ = env_->GetByteArrayElements(array, &isCopy);
      length_ = env_->GetArrayLength(array);
    }
  }
  ByteArrayHolder(const ByteArrayHolder&) = delete;
  ByteArrayHolder& operator=(const ByteArrayHolder&) = delete;

  jbyte* data() const { return data_; }

  size_t length() const { return length_; }

  void commit(bool doCommit = true) { commit_ = doCommit; }

  ~ByteArrayHolder() {
    if (array_) {
      env_->ReleaseByteArrayElements(array_, data(), commit_ ? JNI_COMMIT : JNI_ABORT);
    }
  }
};

inline jenny::LocalRef<jbyteArray> makeByteArray(size_t len, const void* data = nullptr) {
  jenny::Env env;
  jenny::LocalRef<jbyteArray> array(env->NewByteArray(len));
  if (data) {
    jboolean isCopy;
    auto ptr = env->GetByteArrayElements(array.get(), &isCopy);
    std::memcpy(ptr, data, len);
    env->ReleaseByteArrayElements(array.get(), ptr, JNI_COMMIT);
  }
  return array;
}

inline void copyFromByteArray(const LocalRef<jbyteArray>& array, void* dst, size_t len) {
  jenny::Env env;
  jboolean copy;
  auto buf = env->GetByteArrayElements(array.get(), &copy);
  if (len == 0) {
    len = env->GetArrayLength(array.get());
  }
  std::memcpy(dst, buf, len);
  env->ReleaseByteArrayElements(array.get(), buf, JNI_ABORT);
}

inline void copyToByteArray(const LocalRef<jbyteArray>& array, const void* src, size_t len) {
  jenny::Env env;
  jboolean copy;
  auto buf = env->GetByteArrayElements(array.get(), &copy);
  if (len == 0) {
    len = env->GetArrayLength(array.get());
  }
  std::memcpy(buf, src, len);
  env->ReleaseByteArrayElements(array.get(), buf, JNI_COMMIT);
}

/**
 * \code
 *
 * TryCatch tryCatch0;
 * {
 *   TryCatch tryCatch;
 *   jenv->Throw(static_cast<jthrowable>(runtimeException));
 *   assert(tryCatch.hasCaught());
 *   assert(tryCatch.getAndClearException());
 * }
 * assert(!tryCatch0.hasCaught());
 *
 * {
 *   TryCatch tryCatch;
 *   jenv->Throw(static_cast<jthrowable>(runtimeException));
 *   auto ex = tryCatch.getAndClearException();
 *   tryCatch.throwException(ex);
 * }
 * assert(tryCatch0.hasCaught());
 * tryCatch0.clearException();
 *
 * {
 *   TryCatch tryCatch;
 *   jenv->Throw(static_cast<jthrowable>(runtimeException.getThis(false).get()));
 *   tryCatch.rethrowException();
 * }
 *  assert(tryCatch0.hasCaught());
 *
 * \endcode
 */
class TryCatch {
 private:
  JNIEnv* _env;
  bool _rethrow;

 public:
  explicit TryCatch(JNIEnv* env) : _env(env), _rethrow(false) { assert(!env->ExceptionCheck()); }
  explicit TryCatch() : TryCatch(Env().get()) {}

  TryCatch(const TryCatch&) = delete;
  TryCatch& operator=(const TryCatch&) = delete;

  bool hasCaught() const { return _env->ExceptionCheck(); }

  /**
   * get current exception if any, and clear it.
   *
   * note: In order to do any JNI operations after words, the  "current exception" must be cleared
   * first, otherwise JVM would abort.
   */
  LocalRef<jthrowable> getAndClearException() {
    jthrowable e = _env->ExceptionOccurred();
    if (e) {
      _env->ExceptionClear();
    }
    return LocalRef<jthrowable>(_env, e);
  }

  /**
   * clear current exception.
   * @return true for has pending and cleared. false for no pending exception.
   */
  bool clearException() {
    if (hasCaught()) {
      _env->ExceptionClear();
      return true;
    }
    return false;
  }

  /**
   * rethrow an exception, and this TryCatch won't do clear exception in dtor again.
   */
  void throwException(const LocalRef<jthrowable>& e) {
    if (e) {
      _rethrow = true;
      _env->Throw(e.get());
    }
  }

  /**
   * If hasCaught, rethrow the current exception (won't do clear exception in dtor again.)
   * @return has pending exception or not
   */
  bool rethrowException() {
    if (hasCaught()) {
      _rethrow = true;
      return true;
    }
    return false;
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
      state->_jvm->DetachCurrentThread();
    });
  }
};

inline Env::StaticState* Env::staticState() {
  static Env::StaticState state{};
  return &state;
}

inline void Env::attachJvm(JavaVM* jvm) {
  auto state = staticState();
  state->_jvm = jvm;
}

inline JNIEnv* Env::attachCurrentThreadIfNeed() {
  JNIEnv* env;
  auto state = staticState();
  assert(state->_jvm &&
         "please call ::jenny::Env::attachJvm before any usage. (JNI_OnLoad is recommended.)");
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

inline void Env::attachJvm(JavaVM* jvm) {
  assert(jvm);
  auto state = staticState();
  state->_jvm = jvm;
}

inline JNIEnv* Env::attachCurrentThreadIfNeed() {
  JNIEnv* env;
  auto state = staticState();
  assert(state->_jvm &&
      "please call ::jenny::Env::attachJvm before any usage. (JNI_OnLoad is recommended.)");
  auto jvm = state->_jvm;
  if (jvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) == JNI_EDETACHED) {
    struct EnvWrapper {
      JavaVM* const jvm;
      JNIEnv* env;

      explicit EnvWrapper(JavaVM* _jvm) : jvm(_jvm), env(nullptr) {
        _jvm->AttachCurrentThread(reinterpret_cast<void**>(&env), nullptr);
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

#ifdef JENNY_JNI_HELPER_TEST
namespace internal {

// UnitTest
inline void jniHelperUnitTest(JNIEnv* env_) {
  Env::attachJvm(env_);

  Env env;
  assert(env.get() == env_);

  {
    LocalRef<jstring> str = toJavaString("hello");
    assert(str);
    LocalRef<jstring> str_copy = str;
    assert(str_copy);

    LocalRef<jstring> str_notOwn(str.get(), false);
    assert(str_notOwn);

    assert(env->IsSameObject(str.get(), str_copy.get()));
    assert(env->IsSameObject(str.get(), str_notOwn.get()));

    LocalRef<jstring> move = std::move(str);
    assert(move);
    assert(!str);
    move = toJavaString("world");
    assert(!env->IsSameObject(move.get(), str_copy.get()));
    assert(move);
  }

  {
    LocalRef<jstring> str = toJavaString("hello");
    GlobalRef<jstring> glb(str);
    assert(env->IsSameObject(glb.get(), str.get()));
    GlobalRef<jstring> glb_copy = glb;
    assert(glb_copy);

    assert(env->IsSameObject(glb.get(), glb_copy.get()));

    GlobalRef<jstring> glb_move = std::move(glb);
    assert(glb_move);
    assert(!glb);
    glb_move = GlobalRef<jstring>(toJavaString("world"));
    assert(!env->IsSameObject(glb_move.get(), glb_copy.get()));
    assert(glb_move);
  }

  {
    std::string h = "hello";
    LocalRef<jstring> str = toJavaString(h.c_str());
    assert(h == fromJavaString(str));
    StringHolder sh(str);
#ifdef __cpp_lib_string_view
    assert(sh.view() == h);
#endif
  }

  {
    auto bytes = makeByteArray(1024);
    uint8_t data[] = {0, 1, 2, 3, 4, 5, 6, 7};
    auto len = sizeof(data) / sizeof(data[0]);
    copyToByteArray(bytes, data, len);
    {
      ByteArrayHolder holder(bytes);
      assert(holder.length() == 1024);
      assert(holder.data()[0] == 0);
      assert(holder.data()[4] == 4);
      assert(holder.data()[7] == 7);
      assert(holder.data()[8] == 0);
    }

    {
      std::memset(data, 0, len);
      copyFromByteArray(bytes, data, len-1);
      assert(data[0] == 0);
      assert(data[4] == 4);
      assert(data[7] == 0);
    }
  }

  {
    LocalRef<jclass> runtimeExceptionClass(env->FindClass("java/lang/RuntimeException"));

    TryCatch tryCatch0;
    {
      TryCatch tryCatch;
      env->ThrowNew(runtimeExceptionClass.get(), nullptr);
      assert(tryCatch.hasCaught());
      assert(tryCatch.getAndClearException());
    }
    assert(!tryCatch0.hasCaught());

    {
      TryCatch tryCatch;
      env->ThrowNew(runtimeExceptionClass.get(), nullptr);
      auto ex = tryCatch.getAndClearException();
      tryCatch.throwException(ex);
    }
    assert(tryCatch0.hasCaught());
    tryCatch0.clearException();

    {
      TryCatch tryCatch;
      env->ThrowNew(runtimeExceptionClass.get(), nullptr);
      tryCatch.rethrowException();
    }
    assert(tryCatch0.hasCaught());
  }
}

}  // namespace internal

#endif //JENNY_JNI_HELPER_TEST

}  // namespace jenny
