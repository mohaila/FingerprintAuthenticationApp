package ca.qc.mtl.mohaila.fingerprintauthenticationapp.fingerprint

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.qc.mtl.mohaila.fingerprintauthenticationapp.R
import kotlinx.android.synthetic.main.activity_fingerprint.*

class FingerprintActivity : AppCompatActivity(), FingerprintContract.View {
    private lateinit var presenter: FingerprintPresenter

    override val context: Context
        get() {
            return this
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)
    }

    override fun onResume() {
        super.onResume()

        presenter = FingerprintPresenter(this)
    }

    override fun setMessageText(msg: String) {
        errorText.text = msg
    }

    override fun setMessageColor(id: Int) {
        errorText.setTextColor(getColor(id))
    }

}
