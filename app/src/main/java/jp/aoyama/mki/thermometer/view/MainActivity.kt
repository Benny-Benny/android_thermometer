package jp.aoyama.mki.thermometer.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import jp.aoyama.mki.thermometer.R
import jp.aoyama.mki.thermometer.infrastructure.workmanager.ExportAttendanceWorker


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0f

        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))
        ExportAttendanceWorker.startWork(applicationContext)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}