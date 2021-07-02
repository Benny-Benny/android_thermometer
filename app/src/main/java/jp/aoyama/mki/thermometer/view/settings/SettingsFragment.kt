package jp.aoyama.mki.thermometer.view.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import jp.aoyama.mki.thermometer.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
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

    companion object {
        private const val TAG = "SettingsFragment"
    }
}