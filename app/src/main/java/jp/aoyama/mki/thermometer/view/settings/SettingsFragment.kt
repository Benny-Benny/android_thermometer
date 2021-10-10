package jp.aoyama.mki.thermometer.view.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.infrastructure.calendar.ExportAttendanceToGoogleCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val exportButton = findPreference<Preference>(getString(R.string.key_export_attendance))
        exportButton?.setOnPreferenceClickListener {
            exportAttendance()
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
            getString(R.string.key_google_calendar_id),
            getString(R.string.key_google_spreadsheet_id) -> showMessageDialog("設定を反映するには再起動が必要です")
        }
    }

    private fun exportAttendance() {
        val service = ExportAttendanceToGoogleCalendar(requireContext())
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            // 6時間前からのデータを記録
            val oneHourBefore = Calendar.getInstance().apply {
                val currentHour = get(Calendar.HOUR_OF_DAY)
                set(Calendar.HOUR_OF_DAY, currentHour - 24)
            }

            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "カレンダーに出力しています", Toast.LENGTH_LONG).show()
            }

            service.export(oneHourBefore)

            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "カレンダーに出力しました", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showMessageDialog(message: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.close) { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }
}