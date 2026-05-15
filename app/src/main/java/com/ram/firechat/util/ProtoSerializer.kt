package com.ram.firechat.util

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.ram.firechat.UserSettings
import java.io.InputStream
import java.io.OutputStream

object ProtoSerializer : Serializer<UserSettings> {
    override val defaultValue: UserSettings = UserSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserSettings {
        try {
            return UserSettings.parseFrom(input)
        }catch (e: Exception) {
            throw CorruptionException("Data exception in .proto - ${e.message}", e)
        }
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        t.writeTo(output)
    }
}