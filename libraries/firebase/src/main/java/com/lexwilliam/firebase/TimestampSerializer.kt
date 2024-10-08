package com.lexwilliam.firebase

import com.google.firebase.Timestamp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object TimestampSerializer : KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Timestamp") {
            element<Long>("seconds")
            element<Int>("nanoseconds")
        }

    override fun deserialize(decoder: Decoder): Timestamp {
        return decoder.decodeStructure(descriptor) {
            var seconds = 0L
            var nanoseconds = 0
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> seconds = decodeLongElement(descriptor, 0)
                    1 -> nanoseconds = decodeIntElement(descriptor, 1)
                    else -> break
                }
            }
            Timestamp(seconds, nanoseconds)
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: Timestamp
    ) {
        encoder.encodeStructure(descriptor) {
            encodeLongElement(descriptor, 0, value.seconds)
            encodeIntElement(descriptor, 1, value.nanoseconds)
        }
    }
}