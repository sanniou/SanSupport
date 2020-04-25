package com.sanniou.support.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class IgnoreJsonString2Json

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class IgnoreJsonString2List

class IgnoreString2Object : JsonAdapter.Factory {

    override fun create(
        type: Type,
        annotations: Set<Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (annotations.isEmpty()) {
            return null
        }

        for (annotation in annotations) {

            if (annotation is IgnoreJsonString2Json) {
                return handlerString2Json(moshi, type, annotations)
            } else if (annotation is IgnoreJsonString2List) {
                return handlerString2List(moshi, type, annotations)
            }
        }
        return null
    }

    private fun handlerString2Json(
        moshi: Moshi,
        type: Type,
        annotations: Set<Annotation>
    ): JsonAdapter<Any?> {
        val delegate =
            moshi.nextAdapter<Any>(
                this,
                type,
                Types.nextAnnotations(
                    annotations,
                    IgnoreJsonString2Json::class.java
                ) as Set<Annotation>
            )

        return object : JsonAdapter<Any?>() {

            override fun fromJson(reader: JsonReader): Any? {
                val peek = reader.peek()
                if (peek == JsonReader.Token.STRING) {
                    reader.skipValue()
                    return delegate.fromJson("{}")
                }
                return delegate.fromJson(reader)
            }

            override fun toJson(
                writer: JsonWriter,
                value: Any?
            ) {
                delegate.toJson(writer, value)
            }
        }
    }

    private fun handlerString2List(
        moshi: Moshi,
        type: Type,
        annotations: Set<Annotation>
    ): JsonAdapter<Any?> {
        val delegate =
            moshi.nextAdapter<Any>(
                this,
                type,
                Types.nextAnnotations(
                    annotations,
                    IgnoreJsonString2List::class.java
                ) as Set<Annotation>
            )

        return object : JsonAdapter<Any?>() {

            override fun fromJson(reader: JsonReader): Any? {
                val peek = reader.peek()
                if (peek == JsonReader.Token.STRING) {
                    reader.skipValue()
                    return delegate.fromJson("[]")
                }
                return delegate.fromJson(reader)
            }

            override fun toJson(
                writer: JsonWriter,
                value: Any?
            ) {
                delegate.toJson(writer, value)
            }
        }
    }
}