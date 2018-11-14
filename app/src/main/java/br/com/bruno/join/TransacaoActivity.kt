package br.com.bruno.join

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class TransacaoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resume_card)
        //setupView()
    }

    /*fun setupView() {
        var isReceita = true
        llTipoTransacao.setOnClickListener {

            TransitionManager.beginDelayedTransition(llTipoTransacao, ChangeBounds().setPathMotion(ArcMotion()).setDuration(400))
            llToogleParent.gravity = if(isReceita) Gravity.END else Gravity.START

            TransitionManager.beginDelayedTransition(llTipoTransacao, Recolor())
            textReceita.setTextColor(ContextCompat.getColor(this, if (isReceita) R.color.gray else R.color.whiteDark))
            textDespesa.setTextColor(ContextCompat.getColor(this, if (isReceita) R.color.whiteDark else R.color.gray))
            llToogle.setBackgroundResource(if (isReceita) R.drawable.background_accent else R.drawable.background_primary)

            editValue.setTextColor(ContextCompat.getColor(this, if (isReceita) R.color.colorAccent else R.color.colorPrimary))
            isReceita = !isReceita
        }
    }*/
}
