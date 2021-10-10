package jp.aoyama.mki.thermometer.infrastructure.api

import android.content.Context
import androidx.preference.PreferenceManager
import jp.aoyama.mki.thermometer.R

class ApiRepositoryUtil(private val context: Context) {
    val baseUrl: String
        get() {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val keyApiEndPoint = context.getString(R.string.key_api_end_point)

            val defaultApiEndPoint = context.getString(R.string.default_api_endpoint)

            // 設定でエンドポイントが変更されていたら、そちらを利用する。
            return sharedPreferences.getString(keyApiEndPoint, "").let {
                if (it != null && it.isNotBlank()) it
                else defaultApiEndPoint
            }
        }
}