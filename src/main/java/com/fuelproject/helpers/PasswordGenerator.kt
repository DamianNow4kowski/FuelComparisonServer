package com.fuelproject.helpers

import java.security.SecureRandom


class PasswordGenerator {
    fun generatePassword(len: Int): String {
        val dic = ALPHA_CAPS + ALPHA + NUMERIC + SPECIAL_CHARS
        var result = ""
        for (i in 0 until len) {
            val index = random.nextInt(dic.length)
            result += dic[index]
        }
        return result
    }

    companion object {
        private const val ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val ALPHA = "abcdefghijklmnopqrstuvwxyz"
        private const val NUMERIC = "0123456789"
        private const val SPECIAL_CHARS = "!@#$%^&*_=+-/"
        private val random = SecureRandom()
    }
}
