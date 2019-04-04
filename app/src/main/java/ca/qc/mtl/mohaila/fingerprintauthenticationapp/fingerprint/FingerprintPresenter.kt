package ca.qc.mtl.mohaila.fingerprintauthenticationapp.fingerprint

import android.Manifest.permission.USE_FINGERPRINT
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context.FINGERPRINT_SERVICE
import android.content.Context.KEYGUARD_SERVICE
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.security.keystore.KeyProperties
import android.support.v4.app.ActivityCompat
import ca.qc.mtl.mohaila.fingerprintauthenticationapp.R
import java.security.KeyStore
import javax.crypto.KeyGenerator
import android.security.keystore.KeyGenParameterSpec
import java.lang.Exception
import javax.crypto.Cipher
import javax.crypto.SecretKey


class FingerprintPresenter(private val view: FingerprintContract.View) :
    FingerprintManager.AuthenticationCallback(),
    FingerprintContract.Presenter {

    private lateinit var keyStore: KeyStore
    private lateinit var cipher: Cipher

    private val context = view.context
    init {
        val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val fingerprintManager = context.getSystemService(FINGERPRINT_SERVICE) as FingerprintManager
        if (!fingerprintManager.isHardwareDetected) {
            view.setMessageText(context.getString(R.string.no_fingerprint_error))
        } else {
            if (ActivityCompat.checkSelfPermission(context, USE_FINGERPRINT) != PERMISSION_GRANTED) {
                view.setMessageText(context.getString(R.string.no_fingerprint_permission))
            } else {
                if (!fingerprintManager.hasEnrolledFingerprints())
                    view.setMessageText(context.getString(R.string.no_enrolled_fingerprint))
                else {
                    if (!keyguardManager.isKeyguardSecure)
                        view.setMessageText(context.getString(R.string.lock_not_enabled))
                    else {
                        generateKey()
                        if (initCipher()) {
                            val cryptoObject = FingerprintManager.CryptoObject(cipher)
                            startAuth(fingerprintManager, cryptoObject)
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(
                KeyGenParameterSpec
                    .Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            keyGenerator.generateKey()
        } catch (_: Exception) {
            view.setMessageText(context.getString(R.string.genkey_err))
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initCipher(): Boolean{
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (_: Exception) {
            view.setMessageText(context.getString(R.string.err_cipher))
        }
        return false
    }

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        val cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(context, USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        update("Fingerprint Authentication error\n$errString", false)
    }


    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        update("Fingerprint Authentication help\n$helpString", false)
    }


    override fun onAuthenticationFailed() {
        update("Fingerprint Authentication failed.", false)
    }


    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        update("Fingerprint Authentication succeeded.", true)
    }

    private fun update(msg: String, success: Boolean) {
        view.setMessageText(msg)
        if (success)
            view.setMessageColor(R.color.successText)
    }

    companion object {
        const val KEY_NAME = "Fingerprint"
    }
}