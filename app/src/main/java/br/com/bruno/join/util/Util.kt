package br.com.bruno.join.util

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object Util {

    fun showKeyBoard(activity: Activity, editText: EditText) {
        Handler().postDelayed({
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 250)

    }

    fun hideKeyBoard(context: Context, editText: View) {
        Handler().postDelayed({
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }, 250)
    }
}