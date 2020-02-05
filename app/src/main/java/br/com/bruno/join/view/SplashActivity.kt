package br.com.bruno.join.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import br.com.bruno.join.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            val inte = Intent(this, MainActivity::class.java)
            startActivity(inte)
            finish()
        }, 1000)
    }
}
