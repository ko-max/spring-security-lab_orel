package ua.kpi.its.lab.security.util

import java.security.KeyPair
import java.security.KeyPairGenerator

/**
 * Singleton object responsible for providing a cached RSA key pair.
 * This object ensures that a key pair is generated only once and is reused.
 */
object CustomRSAKey {
    @Volatile
    private var keyPair: KeyPair? = null

    /**
     * Provides a cached RSA key pair.
     * This method uses double-checked locking to ensure that the key pair is generated only once.
     * Subsequent calls return the cached key pair.
     *
     * @return [KeyPair] An RSA key pair.
     */
    fun getKeyPair() =
        keyPair ?: synchronized(this) {
            keyPair ?: generateRsaKey().also { keyPair = it }
        }

    /**
     * Generates an RSA key pair using the RSA algorithm.
     * This method configures a [KeyPairGenerator] to generate keys with a length of 2048 bits.
     *
     * @return [KeyPair] Newly generated RSA key pair.
     * @throws IllegalStateException If the RSA algorithm is not available or fails to generate the keys.
     */
    private fun generateRsaKey(): KeyPair =
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            keyPairGenerator.genKeyPair()
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
}