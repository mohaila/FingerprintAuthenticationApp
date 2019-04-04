package ca.qc.mtl.mohaila.fingerprintauthenticationapp.fingerprint

import android.content.Context

interface FingerprintContract {
    interface View {
        val context: Context
        fun setMessageText(msg: String)
        fun setMessageColor(id: Int)
    }

    interface Presenter {

    }
}