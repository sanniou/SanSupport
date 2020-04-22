package com.sanniou.support.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class IgnoreString2Object : JsonAdapter.Factory {
    @Retention(AnnotationRetention.RUNTIME)
    @JsonQualifier
    annotation class IgnoreJsonString2Object

    override fun create(
        type: Type,
        annotations: Set<Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (annotations.isEmpty()) {
            return null
        }

        for (annotation in annotations) {
            if (annotation is IgnoreJsonString2Object) {
                val delegate =
                    moshi.nextAdapter<Any>(
                        this,
                        type,
                        Types.nextAnnotations(
                            annotations,
                            IgnoreJsonString2Object::class.java
                        ) as Set<Annotation>
                    )

                return object : JsonAdapter<Any?>() {

                    override fun fromJson(reader: JsonReader): Any? {
                        val peek = reader.peek()
                        if (peek != JsonReader.Token.STRING) {
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
        }
        return null
    }
}