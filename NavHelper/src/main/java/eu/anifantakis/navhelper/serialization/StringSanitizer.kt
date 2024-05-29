package eu.anifantakis.navhelper.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * A custom serializer for handling URL-encoded strings.
 *
 * This object provides methods for serializing and deserializing strings
 * using URL encoding, which is useful for safely transmitting strings with
 * special characters.
 */
object StringSanitizer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UrlEncodedString", PrimitiveKind.STRING)

    /**
     * Serializes a given string using URL encoding.
     *
     * @param encoder The encoder to use for serialization.
     * @param value The string value to be serialized.
     */
    override fun serialize(encoder: Encoder, value: String) {
        val encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
        encoder.encodeString(encoded)
    }

    /**
     * Deserializes a given string using URL decoding.
     *
     * @param decoder The decoder to use for deserialization.
     * @return The deserialized string value.
     */
    override fun deserialize(decoder: Decoder): String {
        return URLDecoder.decode(decoder.decodeString(), StandardCharsets.UTF_8.toString())
    }
}
