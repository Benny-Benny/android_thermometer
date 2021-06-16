package jp.aoyama.mki.thermometer.view.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.aoyama.mki.thermometer.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(mBinding.fragmentContainer.id, SettingsFragment())
            .commit()
    }
}