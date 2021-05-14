package jp.aoyama.a5819009a5819044a5819104.thermometer.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.gson.Gson
import jp.aoyama.a5819009a5819044a5819104.thermometer.util.CSVFileManager
import jp.aoyama.a5819009a5819044a5819104.thermometer.R


class MainActivity : AppCompatActivity() {
    private val mCsvFileManager = CSVFileManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share -> shareFile()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareFile() {
        val file = mCsvFileManager.getFileUrl(this)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, file)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "体温のデータを出力"))
    }


}