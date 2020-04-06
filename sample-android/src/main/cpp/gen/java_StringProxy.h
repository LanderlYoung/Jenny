/**
 * File generated by Jenny -- https://github.com/LanderlYoung/Jenny
 *
 * DO NOT EDIT THIS FILE.
 *
 * For bug report, please refer to github issue tracker https://github.com/LanderlYoung/Jenny/issues.
 */
#pragma once

#include <jni.h>
#include <assert.h>                        
#include <atomic>
#include <mutex>

namespace java {
class StringProxy {

public:
    static constexpr auto FULL_CLASS_NAME = "java/lang/String";



private:
    // thread safe init
    static std::atomic_bool sInited;
    static std::mutex sInitLock;

    JNIEnv* mJniEnv;
    jobject mJavaObjectReference;

public:

    static bool initClazz(JNIEnv* env);
    
    static void releaseClazz(JNIEnv* env);

    static void assertInited(JNIEnv* env) {
        auto initClazzSuccess = initClazz(env);
        assert(initClazzSuccess);
    }

    StringProxy(JNIEnv* env, jobject javaObj)
            : mJniEnv(env), mJavaObjectReference(javaObj) {
        if (env) { assertInited(env); }
    }

    StringProxy(const StringProxy& from) = default;
    StringProxy &operator=(const StringProxy &) = default;

    StringProxy(StringProxy&& from) noexcept
           : mJniEnv(from.mJniEnv), mJavaObjectReference(from.mJavaObjectReference) {
        from.mJavaObjectReference = nullptr;
    }
    
    StringProxy& operator=(StringProxy&& from) noexcept {
       mJniEnv = from.mJniEnv;
       std::swap(mJavaObjectReference, from.mJavaObjectReference);
       return *this;
   }

    ~StringProxy() = default;
    
    // helper method to get underlay jobject reference
    jobject operator*() const {
       return mJavaObjectReference;
    }
    
    // helper method to check underlay jobject reference is not nullptr
    operator bool() const {
       return mJavaObjectReference;
    }
    
    // helper method to delete JNI local ref.
    // use only when you really understand JNIEnv::DeleteLocalRef.
    void deleteLocalRef() {
       if (mJavaObjectReference) {
           mJniEnv->DeleteLocalRef(mJavaObjectReference);
           mJavaObjectReference = nullptr;
       }
    }
    
    // === java methods below ===
    
    // construct: public String()
    static StringProxy newInstance(JNIEnv* env) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_0));
    } 
    
    // construct: public String(java.lang.String original)
    static StringProxy newInstance(JNIEnv* env, jstring original) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_1, original));
    } 
    
    // construct: public String(char[] value)
    static StringProxy newInstance(JNIEnv* env, jcharArray value) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_2, value));
    } 
    
    // construct: public String(char[] value, int offset, int count)
    static StringProxy newInstance(JNIEnv* env, jcharArray value, jint offset, jint count) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_3, value, offset, count));
    } 
    
    // construct: public String(int[] codePoints, int offset, int count)
    static StringProxy newInstance(JNIEnv* env, jintArray codePoints, jint offset, jint count) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_4, codePoints, offset, count));
    } 
    
    // construct: public String(byte[] ascii, int hibyte, int offset, int count)
    static StringProxy newInstance(JNIEnv* env, jbyteArray ascii, jint hibyte, jint offset, jint count) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_5, ascii, hibyte, offset, count));
    } 
    
    // construct: public String(byte[] ascii, int hibyte)
    static StringProxy newInstance(JNIEnv* env, jbyteArray ascii, jint hibyte) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_6, ascii, hibyte));
    } 
    
    // construct: public String(byte[] bytes, int offset, int length, java.lang.String charsetName)
    static StringProxy newInstance(JNIEnv* env, jbyteArray bytes, jint offset, jint length, jstring charsetName) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_7, bytes, offset, length, charsetName));
    } 
    
    // construct: public String(byte[] bytes, int offset, int length, java.nio.charset.Charset charset)
    static StringProxy newInstance(JNIEnv* env, jbyteArray bytes, jint offset, jint length, jobject charset) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_8, bytes, offset, length, charset));
    } 
    
    // construct: public String(byte[] bytes, java.lang.String charsetName)
    static StringProxy newInstance(JNIEnv* env, jbyteArray bytes, jstring charsetName) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_9, bytes, charsetName));
    } 
    
    // construct: public String(byte[] bytes, java.nio.charset.Charset charset)
    static StringProxy newInstance(JNIEnv* env, jbyteArray bytes, jobject charset) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_10, bytes, charset));
    } 
    
    // construct: public String(byte[] bytes, int offset, int length)
    static StringProxy newInstance(JNIEnv* env, jbyteArray bytes, jint offset, jint length) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_11, bytes, offset, length));
    } 
    
    // construct: public String(byte[] bytes)
    static StringProxy newInstance(JNIEnv* env, jbyteArray bytes) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_12, bytes));
    } 
    
    // construct: public String(java.lang.StringBuffer buffer)
    static StringProxy newInstance__Ljava_lang_StringBuffer_2(JNIEnv* env, jobject buffer) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_13, buffer));
    } 
    
    // construct: public String(java.lang.StringBuilder builder)
    static StringProxy newInstance__Ljava_lang_StringBuilder_2(JNIEnv* env, jobject builder) noexcept {
       assertInited(env);
       return StringProxy(env, env->NewObject(sClazz, sConstruct_14, builder));
    } 
    

    // method: public int length()
    jint length() const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_length_0);
    }

    // method: public boolean isEmpty()
    jboolean isEmpty() const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_isEmpty_0);
    }

    // method: public char charAt(int arg0)
    jchar charAt(jint arg0) const {
        return mJniEnv->CallCharMethod(mJavaObjectReference, sMethod_charAt_0, arg0);
    }

    // method: public int codePointAt(int index)
    jint codePointAt(jint index) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_codePointAt_0, index);
    }

    // method: public int codePointBefore(int index)
    jint codePointBefore(jint index) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_codePointBefore_0, index);
    }

    // method: public int codePointCount(int beginIndex, int endIndex)
    jint codePointCount(jint beginIndex, jint endIndex) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_codePointCount_0, beginIndex, endIndex);
    }

    // method: public int offsetByCodePoints(int index, int codePointOffset)
    jint offsetByCodePoints(jint index, jint codePointOffset) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_offsetByCodePoints_0, index, codePointOffset);
    }

    // method: public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)
    void getChars(jint srcBegin, jint srcEnd, jcharArray dst, jint dstBegin) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_getChars_0, srcBegin, srcEnd, dst, dstBegin);
    }

    // method: public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin)
    void getBytes(jint srcBegin, jint srcEnd, jbyteArray dst, jint dstBegin) const {
        mJniEnv->CallVoidMethod(mJavaObjectReference, sMethod_getBytes_0, srcBegin, srcEnd, dst, dstBegin);
    }

    // method: public byte[] getBytes(java.lang.String charsetName)
    jbyteArray getBytes(jstring charsetName) const {
        return reinterpret_cast<jbyteArray>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_getBytes_1, charsetName));
    }

    // method: public byte[] getBytes(java.nio.charset.Charset charset)
    jbyteArray getBytes(jobject charset) const {
        return reinterpret_cast<jbyteArray>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_getBytes_2, charset));
    }

    // method: public byte[] getBytes()
    jbyteArray getBytes() const {
        return reinterpret_cast<jbyteArray>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_getBytes_3));
    }

    // method: public boolean equals(java.lang.Object anObject)
    jboolean equals(jobject anObject) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_equals_0, anObject);
    }

    // method: public boolean contentEquals(java.lang.StringBuffer sb)
    jboolean contentEquals__Ljava_lang_StringBuffer_2(jobject sb) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_contentEquals_0, sb);
    }

    // method: public boolean contentEquals(java.lang.CharSequence cs)
    jboolean contentEquals__Ljava_lang_CharSequence_2(jobject cs) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_contentEquals_1, cs);
    }

    // method: public boolean equalsIgnoreCase(java.lang.String anotherString)
    jboolean equalsIgnoreCase(jstring anotherString) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_equalsIgnoreCase_0, anotherString);
    }

    // method: public int compareTo(java.lang.String arg0)
    jint compareTo(jstring arg0) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_compareTo_0, arg0);
    }

    // method: public int compareToIgnoreCase(java.lang.String str)
    jint compareToIgnoreCase(jstring str) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_compareToIgnoreCase_0, str);
    }

    // method: public boolean regionMatches(int toffset, java.lang.String other, int ooffset, int len)
    jboolean regionMatches(jint toffset, jstring other, jint ooffset, jint len) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_regionMatches_0, toffset, other, ooffset, len);
    }

    // method: public boolean regionMatches(boolean ignoreCase, int toffset, java.lang.String other, int ooffset, int len)
    jboolean regionMatches(jboolean ignoreCase, jint toffset, jstring other, jint ooffset, jint len) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_regionMatches_1, ignoreCase, toffset, other, ooffset, len);
    }

    // method: public boolean startsWith(java.lang.String prefix, int toffset)
    jboolean startsWith(jstring prefix, jint toffset) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_startsWith_0, prefix, toffset);
    }

    // method: public boolean startsWith(java.lang.String prefix)
    jboolean startsWith(jstring prefix) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_startsWith_1, prefix);
    }

    // method: public boolean endsWith(java.lang.String suffix)
    jboolean endsWith(jstring suffix) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_endsWith_0, suffix);
    }

    // method: public int hashCode()
    jint hashCode() const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_hashCode_0);
    }

    // method: public int indexOf(int ch)
    jint indexOf(jint ch) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_indexOf_0, ch);
    }

    // method: public int indexOf(int ch, int fromIndex)
    jint indexOf(jint ch, jint fromIndex) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_indexOf_1, ch, fromIndex);
    }

    // method: public int indexOf(java.lang.String str)
    jint indexOf(jstring str) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_indexOf_2, str);
    }

    // method: public int indexOf(java.lang.String str, int fromIndex)
    jint indexOf(jstring str, jint fromIndex) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_indexOf_3, str, fromIndex);
    }

    // method: public int lastIndexOf(int ch)
    jint lastIndexOf(jint ch) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_lastIndexOf_0, ch);
    }

    // method: public int lastIndexOf(int ch, int fromIndex)
    jint lastIndexOf(jint ch, jint fromIndex) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_lastIndexOf_1, ch, fromIndex);
    }

    // method: public int lastIndexOf(java.lang.String str)
    jint lastIndexOf(jstring str) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_lastIndexOf_2, str);
    }

    // method: public int lastIndexOf(java.lang.String str, int fromIndex)
    jint lastIndexOf(jstring str, jint fromIndex) const {
        return mJniEnv->CallIntMethod(mJavaObjectReference, sMethod_lastIndexOf_3, str, fromIndex);
    }

    // method: public java.lang.String substring(int beginIndex)
    jstring substring(jint beginIndex) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_substring_0, beginIndex));
    }

    // method: public java.lang.String substring(int beginIndex, int endIndex)
    jstring substring(jint beginIndex, jint endIndex) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_substring_1, beginIndex, endIndex));
    }

    // method: public java.lang.CharSequence subSequence(int beginIndex, int endIndex)
    jobject subSequence(jint beginIndex, jint endIndex) const {
        return mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_subSequence_0, beginIndex, endIndex);
    }

    // method: public java.lang.String concat(java.lang.String arg0)
    jstring concat(jstring arg0) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_concat_0, arg0));
    }

    // method: public java.lang.String replace(char oldChar, char newChar)
    jstring replace(jchar oldChar, jchar newChar) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_replace_0, oldChar, newChar));
    }

    // method: public java.lang.String replace(java.lang.CharSequence target, java.lang.CharSequence replacement)
    jstring replace(jobject target, jobject replacement) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_replace_1, target, replacement));
    }

    // method: public boolean matches(java.lang.String regex)
    jboolean matches(jstring regex) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_matches_0, regex);
    }

    // method: public boolean contains(java.lang.CharSequence s)
    jboolean contains(jobject s) const {
        return mJniEnv->CallBooleanMethod(mJavaObjectReference, sMethod_contains_0, s);
    }

    // method: public java.lang.String replaceFirst(java.lang.String regex, java.lang.String replacement)
    jstring replaceFirst(jstring regex, jstring replacement) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_replaceFirst_0, regex, replacement));
    }

    // method: public java.lang.String replaceAll(java.lang.String regex, java.lang.String replacement)
    jstring replaceAll(jstring regex, jstring replacement) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_replaceAll_0, regex, replacement));
    }

    // method: public java.lang.String[] split(java.lang.String regex, int limit)
    jobjectArray split(jstring regex, jint limit) const {
        return reinterpret_cast<jobjectArray>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_split_0, regex, limit));
    }

    // method: public java.lang.String[] split(java.lang.String regex)
    jobjectArray split(jstring regex) const {
        return reinterpret_cast<jobjectArray>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_split_1, regex));
    }

    // method: public static java.lang.String join(java.lang.CharSequence delimiter, java.lang.CharSequence[] elements)
    static jstring join(JNIEnv* env, jobject delimiter, jobjectArray elements) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_join_0, delimiter, elements));
    }

    // method: public static java.lang.String join(java.lang.CharSequence delimiter, java.lang.Iterable<? extends java.lang.CharSequence> elements)
    static jstring join(JNIEnv* env, jobject delimiter, jobject elements) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_join_1, delimiter, elements));
    }

    // method: public java.lang.String toLowerCase(java.util.Locale locale)
    jstring toLowerCase(jobject locale) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_toLowerCase_0, locale));
    }

    // method: public java.lang.String toLowerCase()
    jstring toLowerCase() const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_toLowerCase_1));
    }

    // method: public java.lang.String toUpperCase(java.util.Locale locale)
    jstring toUpperCase(jobject locale) const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_toUpperCase_0, locale));
    }

    // method: public java.lang.String toUpperCase()
    jstring toUpperCase() const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_toUpperCase_1));
    }

    // method: public java.lang.String trim()
    jstring trim() const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_trim_0));
    }

    // method: public java.lang.String toString()
    jstring toString() const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_toString_0));
    }

    // method: public char[] toCharArray()
    jcharArray toCharArray() const {
        return reinterpret_cast<jcharArray>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_toCharArray_0));
    }

    // method: public static java.lang.String format(java.lang.String format, java.lang.Object[] args)
    static jstring format(JNIEnv* env, jstring format, jobjectArray args) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_format_0, format, args));
    }

    // method: public static java.lang.String format(java.util.Locale l, java.lang.String format, java.lang.Object[] args)
    static jstring format(JNIEnv* env, jobject l, jstring format, jobjectArray args) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_format_1, l, format, args));
    }

    // method: public static java.lang.String valueOf(java.lang.Object obj)
    static jstring valueOf(JNIEnv* env, jobject obj) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_0, obj));
    }

    // method: public static java.lang.String valueOf(char[] data)
    static jstring valueOf(JNIEnv* env, jcharArray data) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_1, data));
    }

    // method: public static java.lang.String valueOf(char[] data, int offset, int count)
    static jstring valueOf(JNIEnv* env, jcharArray data, jint offset, jint count) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_2, data, offset, count));
    }

    // method: public static java.lang.String valueOf(boolean b)
    static jstring valueOf(JNIEnv* env, jboolean b) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_3, b));
    }

    // method: public static java.lang.String valueOf(char c)
    static jstring valueOf(JNIEnv* env, jchar c) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_4, c));
    }

    // method: public static java.lang.String valueOf(int i)
    static jstring valueOf(JNIEnv* env, jint i) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_5, i));
    }

    // method: public static java.lang.String valueOf(long l)
    static jstring valueOf(JNIEnv* env, jlong l) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_6, l));
    }

    // method: public static java.lang.String valueOf(float f)
    static jstring valueOf(JNIEnv* env, jfloat f) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_7, f));
    }

    // method: public static java.lang.String valueOf(double d)
    static jstring valueOf(JNIEnv* env, jdouble d) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_valueOf_8, d));
    }

    // method: public static java.lang.String copyValueOf(char[] data, int offset, int count)
    static jstring copyValueOf(JNIEnv* env, jcharArray data, jint offset, jint count) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_copyValueOf_0, data, offset, count));
    }

    // method: public static java.lang.String copyValueOf(char[] data)
    static jstring copyValueOf(JNIEnv* env, jcharArray data) {
        assertInited(env);
        return reinterpret_cast<jstring>(env->CallStaticObjectMethod(sClazz, sMethod_copyValueOf_1, data));
    }

    // method: public java.lang.String intern()
    jstring intern() const {
        return reinterpret_cast<jstring>(mJniEnv->CallObjectMethod(mJavaObjectReference, sMethod_intern_0));
    }


    // field: public static final java.util.Comparator<java.lang.String> CASE_INSENSITIVE_ORDER
    static jobject getCASE_INSENSITIVE_ORDER(JNIEnv* env) {
       assertInited(env);
       return env->GetStaticObjectField(sClazz, sField_CASE_INSENSITIVE_ORDER_0);

    }

    // field: public static final java.util.Comparator<java.lang.String> CASE_INSENSITIVE_ORDER
    static void setCASE_INSENSITIVE_ORDER(JNIEnv* env, jobject CASE_INSENSITIVE_ORDER) {
        assertInited(env);
        env->SetStaticObjectField(sClazz, sField_CASE_INSENSITIVE_ORDER_0, CASE_INSENSITIVE_ORDER);
    }



private:
    static jclass sClazz;
    static jmethodID sConstruct_0;
    static jmethodID sConstruct_1;
    static jmethodID sConstruct_2;
    static jmethodID sConstruct_3;
    static jmethodID sConstruct_4;
    static jmethodID sConstruct_5;
    static jmethodID sConstruct_6;
    static jmethodID sConstruct_7;
    static jmethodID sConstruct_8;
    static jmethodID sConstruct_9;
    static jmethodID sConstruct_10;
    static jmethodID sConstruct_11;
    static jmethodID sConstruct_12;
    static jmethodID sConstruct_13;
    static jmethodID sConstruct_14;

    static jmethodID sMethod_length_0;
    static jmethodID sMethod_isEmpty_0;
    static jmethodID sMethod_charAt_0;
    static jmethodID sMethod_codePointAt_0;
    static jmethodID sMethod_codePointBefore_0;
    static jmethodID sMethod_codePointCount_0;
    static jmethodID sMethod_offsetByCodePoints_0;
    static jmethodID sMethod_getChars_0;
    static jmethodID sMethod_getBytes_0;
    static jmethodID sMethod_getBytes_1;
    static jmethodID sMethod_getBytes_2;
    static jmethodID sMethod_getBytes_3;
    static jmethodID sMethod_equals_0;
    static jmethodID sMethod_contentEquals_0;
    static jmethodID sMethod_contentEquals_1;
    static jmethodID sMethod_equalsIgnoreCase_0;
    static jmethodID sMethod_compareTo_0;
    static jmethodID sMethod_compareToIgnoreCase_0;
    static jmethodID sMethod_regionMatches_0;
    static jmethodID sMethod_regionMatches_1;
    static jmethodID sMethod_startsWith_0;
    static jmethodID sMethod_startsWith_1;
    static jmethodID sMethod_endsWith_0;
    static jmethodID sMethod_hashCode_0;
    static jmethodID sMethod_indexOf_0;
    static jmethodID sMethod_indexOf_1;
    static jmethodID sMethod_indexOf_2;
    static jmethodID sMethod_indexOf_3;
    static jmethodID sMethod_lastIndexOf_0;
    static jmethodID sMethod_lastIndexOf_1;
    static jmethodID sMethod_lastIndexOf_2;
    static jmethodID sMethod_lastIndexOf_3;
    static jmethodID sMethod_substring_0;
    static jmethodID sMethod_substring_1;
    static jmethodID sMethod_subSequence_0;
    static jmethodID sMethod_concat_0;
    static jmethodID sMethod_replace_0;
    static jmethodID sMethod_replace_1;
    static jmethodID sMethod_matches_0;
    static jmethodID sMethod_contains_0;
    static jmethodID sMethod_replaceFirst_0;
    static jmethodID sMethod_replaceAll_0;
    static jmethodID sMethod_split_0;
    static jmethodID sMethod_split_1;
    static jmethodID sMethod_join_0;
    static jmethodID sMethod_join_1;
    static jmethodID sMethod_toLowerCase_0;
    static jmethodID sMethod_toLowerCase_1;
    static jmethodID sMethod_toUpperCase_0;
    static jmethodID sMethod_toUpperCase_1;
    static jmethodID sMethod_trim_0;
    static jmethodID sMethod_toString_0;
    static jmethodID sMethod_toCharArray_0;
    static jmethodID sMethod_format_0;
    static jmethodID sMethod_format_1;
    static jmethodID sMethod_valueOf_0;
    static jmethodID sMethod_valueOf_1;
    static jmethodID sMethod_valueOf_2;
    static jmethodID sMethod_valueOf_3;
    static jmethodID sMethod_valueOf_4;
    static jmethodID sMethod_valueOf_5;
    static jmethodID sMethod_valueOf_6;
    static jmethodID sMethod_valueOf_7;
    static jmethodID sMethod_valueOf_8;
    static jmethodID sMethod_copyValueOf_0;
    static jmethodID sMethod_copyValueOf_1;
    static jmethodID sMethod_intern_0;

    static jfieldID sField_CASE_INSENSITIVE_ORDER_0;

};
} // endof namespace java
