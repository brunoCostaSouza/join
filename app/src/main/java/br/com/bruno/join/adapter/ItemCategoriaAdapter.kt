package br.com.bruno.join.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import br.com.bruno.join.R
import br.com.bruno.join.entity.Categoria
import kotlinx.android.synthetic.main.item_categoria.view.*
import kotlinx.android.synthetic.main.item_categoria_empty.view.*

class CategoriaAdapter(
        context: Context,
        var categorias: List<Categoria>
) : ArrayAdapter<Categoria>(context, R.layout.item_categoria, R.id.textDescricaoCategoria, categorias) {

    override fun getCount(): Int {
        return categorias.size
    }

    override fun getItem(position: Int): Categoria? {
        return categorias[position]
    }

    override fun getItemId(position: Int): Long {
        return categorias[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getMyView(position, parent, 18F)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getMyView(position, parent, 20F, true)
    }

    private fun getMyView(position: Int, parent: ViewGroup, fontSize: Float, isDropDown: Boolean = false): View{

        if (!isDropDown && position == 0) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_categoria_empty, parent, false)
            view.textDescricaoCategoriaVazia.textSize = fontSize
            return view
        }

        val item = getItem(position)!!
        val viewCustom = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false)
        if (item.id == -1L) {
            viewCustom.textDescricaoCategoria.visibility = View.GONE
            viewCustom.imgCategoria.visibility = View.GONE
        } else {
            viewCustom.textDescricaoCategoria.textSize = fontSize
            viewCustom.imgCategoria.setBackgroundResource(item.resorce!!)
            viewCustom.textDescricaoCategoria.text = item.descricao
        }


        return viewCustom
    }
}