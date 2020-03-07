package com.bubelov.coins.util

import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class JsonStringConverterFactory(private val delegateFactory: Converter.Factory) : Converter.Factory() {
    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        for (annotation in annotations) {
            if (annotation is Json) {
                val delegate = delegateFactory.requestBodyConverter(
                    type,
                    annotations,
                    arrayOfNulls(0),
                    retrofit
                ) as Converter<Any, RequestBody>

                return DelegateToStringConverter<Any>(delegate)
            }
        }

        return null
    }

    internal class DelegateToStringConverter<T>(private val delegate: Converter<Any, RequestBody>) :
        Converter<T, String> {

        @Throws(IOException::class)
        override fun convert(value: T): String {
            val buffer = Buffer()
            delegate.convert(value)?.writeTo(buffer)
            return buffer.readUtf8()
        }
    }
}