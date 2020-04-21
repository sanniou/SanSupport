package com.sanniou.support.moshi

import com.squareup.moshi.*
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
                        )
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