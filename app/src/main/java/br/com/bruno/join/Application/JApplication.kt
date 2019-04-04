package br.com.bruno.join.Application

import android.app.Application
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import br.com.bruno.join.R
import com.felixsoares.sweetdialog.SweetDialog
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.alert_toast.view.*
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


class JApplication : Application() {

    companion object {
        val ALERT_TYPE_SUCCESS = 1
        val ALERT_TYPE_WARNING = 2
        val ALERT_TYPE_ERROR = 3
        val ALERT_TYPE_INFO = 4
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val config = RealmConfiguration
                .Builder()
                .name("joinbrdb")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)

    }

    fun showAlert(context: Context, description: String, type: SweetDialog.Type, timeShow: Long) {

        SweetDialog()
                .setTitle(description)
                .setType(type)
                .setTimer(timeShow)
                .show(context)

        /*
        Toast(applicationContext).apply {

            val layout = LayoutInflater.from(applicationContext).inflate(R.layout.alert_toast, null, false)

            val resource = when(type){
                ALERT_TYPE_SUCCESS -> R.drawable.ic_sucess
                ALERT_TYPE_WARNING -> R.drawable.ic_warning
                ALERT_TYPE_ERROR -> R.drawable.ic_error
                ALERT_TYPE_INFO -> R.drawable.ic_info
                else -> R.drawable.ic_info
            }

            val color = when(type){
                ALERT_TYPE_SUCCESS -> R.color.sucess
                ALERT_TYPE_WARNING -> R.color.orange
                ALERT_TYPE_ERROR -> R.color.error
                ALERT_TYPE_INFO -> R.color.gray_dark
                else -> R.color.sucess
            }

            val border = when(type){
                ALERT_TYPE_SUCCESS -> R.drawable.button_green
                ALERT_TYPE_WARNING -> R.drawable.button_orange
                ALERT_TYPE_ERROR -> R.drawable.button_pink
                ALERT_TYPE_INFO -> R.drawable.button_green
                else -> R.drawable.button_green
            }

            layout.descriptionAlert.text = description
            layout.descriptionAlert.setTextColor(color)

            layout.rootAlert.setBackgroundResource(border)
            layout.srcAlert.setBackgroundResource(resource)


            view = layout
            duration = timeShow
            setGravity(Gravity.BOTTOM, 0, 175)

            show()
        }*/

    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}