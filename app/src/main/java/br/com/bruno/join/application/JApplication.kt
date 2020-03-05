package br.com.bruno.join.application

import android.app.Application
import android.content.Context
import br.com.bruno.join.R
import com.felixsoares.sweetdialog.SweetDialog
import io.realm.Realm
import io.realm.RealmConfiguration
import br.com.bruno.join.model.Categoria
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save


class JApplication : Application() {

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
        addCategoriesIfNeeded()
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
                ALERT_TYPE_ERROR -> R.drawable.button_round
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

    fun addCategoriesIfNeeded() {
        if (Categoria().queryAll().isEmpty()) {
            Categoria().apply {
                id = 1L
                descricao = "Alimentação"
                resorce = R.drawable.ic_food
                colorIcon = R.color.c_b
                save()
            }

            Categoria().apply {
                id = 2L
                descricao = "Renda"
                resorce = R.drawable.ic_money
                colorIcon = R.color.c_a
                save()
            }

            Categoria().apply {
                id = 3L
                descricao = "Compras"
                resorce = R.drawable.ic_purchase
                colorIcon = R.color.c_g
                save()
            }

            Categoria().apply {
                id = 4L
                descricao = "Viagem"
                resorce = R.drawable.ic_travel
                colorIcon = R.color.c_c
                save()
            }

            Categoria().apply {
                id = 5L
                descricao = "Manutenção"
                resorce = R.drawable.ic_car
                colorIcon = R.color.c_h
                save()
            }

            Categoria().apply {
                id = 6L
                descricao = "Supermercado"
                resorce = R.drawable.ic_supermarket
                colorIcon = R.color.c_i
                save()
            }

            Categoria().apply {
                id = 7L
                descricao = "Cartão de crédito"
                resorce = R.drawable.ic_payment
                colorIcon = R.color.c_j
                save()
            }

            Categoria().apply {
                id = 8L
                descricao = "Combustível"
                resorce = R.drawable.ic_gas
                colorIcon = R.color.c_k
                save()
            }

            Categoria().apply {
                id = 9L
                descricao = "Vida e entreterimento"
                resorce = R.drawable.ic_fan
                colorIcon = R.color.c_l
                save()
            }

            Categoria().apply {
                id = 10L
                descricao = "Outros"
                colorIcon = R.color.c_b
                resorce = R.drawable.ic_others
                save()
            }
        }
    }
}