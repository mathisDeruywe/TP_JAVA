package com.example.listviewcustom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class PersonAdapter(
    private val context: Context,
    private val persons: List<Person>
) : BaseAdapter() {

    override fun getCount(): Int = persons.size

    override fun getItem(position: Int): Any = persons[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_person, parent, false)

        val imgPhoto = view.findViewById<ImageView>(R.id.imgPhoto)
        val txtNom = view.findViewById<TextView>(R.id.txtNom)
        val txtPrenom = view.findViewById<TextView>(R.id.txtPrenom)

        val person = persons[position]

        imgPhoto.setImageResource(person.photo)
        txtNom.text = person.nom
        txtPrenom.text = person.prenom

        return view
    }
}