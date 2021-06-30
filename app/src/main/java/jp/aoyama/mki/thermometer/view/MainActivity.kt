package jp.aoyama.mki.thermometer.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.infrastructure.workmanager.ExportAttendanceWorker


class MainActivity : AppCompatActivity() {

    private val mScopes = ExportAttendanceWorker.scopes.map { Scope(it) }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0f

        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))

        googleSignIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_SIGN_IN -> {
                if (resultCode == RESULT_OK && data != null) {
                    val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(data)
                    requestGooglePermission(googleAccount.result)
                }
            }

            REQUEST_GOOGLE_PERMISSION -> {
                if (resultCode == RESULT_OK)
                    ExportAttendanceWorker.startWork(applicationContext)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun googleSignIn() {
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount != null) {
            requestGooglePermission(googleSignInAccount)
            return
        }

        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        startActivityForResult(googleSignInClient.signInIntent, REQUEST_SIGN_IN)
    }

    private fun requestGooglePermission(googleAccount: GoogleSignInAccount) {
        if (GoogleSignIn.hasPermissions(googleAccount, *mScopes)) {
            ExportAttendanceWorker.startWork(this)
            return
        }

        GoogleSignIn.requestPermissions(
            this,
            REQUEST_GOOGLE_PERMISSION,
            googleAccount,
            *mScopes
        )
    }

    companion object {
        private const val REQUEST_SIGN_IN = 100
        private const val REQUEST_GOOGLE_PERMISSION = 200
    }
}