package com.bubelov.coins.util

import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime

class DateTimeAdapter : TypeAdapter<DateTime>() {
    override fun read(`in`: JsonReader): DateTime? {
        return try {
            when (`in`.peek()) {
                JsonToken.NULL -> {
                    `in`.nextNull()
                    null
                }
                else -> DateTime.parse(`in`.nextString())
            }
        } catch (t: Throwable) {
            throw JsonParseException(t)
        }
    }

    override fun write(out: JsonWriter, value: DateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }
}