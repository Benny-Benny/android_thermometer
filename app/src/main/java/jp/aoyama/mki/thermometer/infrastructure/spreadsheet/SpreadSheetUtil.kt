package jp.aoyama.mki.thermometer.infrastructure.spreadsheet

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ClearValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.infrastructure.calendar.ExportAttendanceToGoogleCalendar

/**
 * SpreadSheetの共通する操作をまとめた Utility クラス
 */
class SpreadSheetUtil(private val mContext: Context) {
    private val mSheetsService: Sheets by lazy {
        val transport = NetHttpTransport.Builder().build()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        val credentialFileName = mContext.getString(R.string.service_account)
        val credentialJson = mContext.resources.assets.open(credentialFileName)
        val credential = GoogleCredential
            .fromStream(credentialJson)
            .createScoped(ExportAttendanceToGoogleCalendar.scopes)

        Sheets.Builder(transport, jsonFactory, credential)
            .setApplicationName(mContext.getString(R.string.app_name))
            .build()
    }

    private val spreadSheetId
        get(): String {
            val defaultId = mContext.getString(R.string.spread_sheet_id)

            // 設定で Spread SheetのID が変更されていたら、そちらを利用する。
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
            val keySpreadSheetId = mContext.getString(R.string.key_google_spreadsheet_id)
            return sharedPreferences.getString(keySpreadSheetId, defaultId) ?: defaultId
        }

    suspend fun getColumnOf(range: String, test: (List<String>) -> Boolean): Int? {
        val row = getValues(range).indexOfFirst { test(it) }
        if (row == -1) return null
        return row + 1
    }

    suspend fun getValues(range: String): List<List<String>> {
        val response = mSheetsService
            .spreadsheets().values()
            .get(spreadSheetId, range)
            .execute()
        val values = response.getValues() ?: return emptyList()
        return values as List<List<String>>
    }

    fun appendValues(range: String, values: List<List<String>>) {
        val body = ValueRange().setValues(values)
        mSheetsService.spreadsheets().values()
            .append(spreadSheetId, range, body)
            .setValueInputOption("RAW")
            .execute()
    }

    fun updateValues(range: String, values: List<List<String>>) {
        val body = ValueRange().setValues(values)
        mSheetsService.spreadsheets().values()
            .update(spreadSheetId, range, body)
            .setValueInputOption("RAW")
            .execute()
    }

    fun clearValues(range: String) {
        mSheetsService.spreadsheets().values()
            .clear(spreadSheetId, range, ClearValuesRequest())
            .execute()
    }
}