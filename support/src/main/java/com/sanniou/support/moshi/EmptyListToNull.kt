package com.sanniou.support.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

/**
 * Converts empty JSON arrays to null for non-collection types. Use for poorly-implemented APIs.
 */
class EmptyListToNull : JsonAdapter.Factory {

    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        if (MutableList::class.java.isAssignableFrom(rawType)
            || MutableSet::class.java.isAssignableFrom(rawType)
            || rawType.isArray
        ) {
            return null // We don't want to decorate actual collection types.
        }
        val delegate = moshi.nextAdapter<Any>(this, type, annotations)

        return object : JsonAdapter<Any?>() {
            override fun fromJson(reader: JsonReader): Any? {
                if (reader.peek() != JsonReader.Token.BEGIN_ARRAY) {
                    return delegate.fromJson(reader)
                }
                reader.beginArray()
                reader.endArray()
                return delegate.fromJson("{}")
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