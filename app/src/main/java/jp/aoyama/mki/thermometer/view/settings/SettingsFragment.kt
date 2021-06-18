package jp.aoyama.mki.thermometer.view.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.infrastructure.export.UserCSVUtil
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val exportUserPreference = findPreference<Preference>(getString(R.string.key_export_users))
        exportUserPreference?.setOnPreferenceClickListener {
            exportUserData()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.key_api_end_point) -> showRelaunchDialog()
        }
    }

    private fun showRelaunchDialog() {
        Log.d(TAG, "showRelaunchAppDialog: CHANGED")
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("設定を反映するには再起動が必要です")
            .setPositiveButton(R.string.ok) { dialog, _ ->
                // アプリを再起動する
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun exportUserData() = lifecycleScope.launch {
        val fileUri = UserCSVUtil().exportToCsv(requireContext())
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "text/csv"
        }

        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.export_user_data)
            )
        )
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}