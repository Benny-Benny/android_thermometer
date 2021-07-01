package jp.aoyama.mki.thermometer.infrastructure.spreadsheet

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ClearValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.infrastructure.workmanager.ExportAttendanceWorker

class SpreadSheetUtil(private val mContext: Context) {
    private val mSheetsService: Sheets by lazy {
        val transport = NetHttpTransport.Builder().build()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        val credentialFileName = mContext.getString(R.string.service_account)
        val credentialJson = mContext.resources.assets.open(credentialFileName)
        val credential = GoogleCredential
            .fromStream(credentialJson)
            .createScoped(ExportAttendanceWorker.scopes)

        Sheets.Builder(transport, jsonFactory, credential)
            .setApplicationName(mContext.getString(R.string.app_name))
            .build()
    }

    private val spreadSheetId = mContext.getString(R.string.spread_sheet_id)

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
            .append(spreadSheetId, range, body)
            .setValueInputOption("RAW")
            .execute()
    }

    fun clearValues(range: String) {
        mSheetsService.spreadsheets().values()
            .clear(spreadSheetId, range, ClearValuesRequest())
            .execute()
    }
}