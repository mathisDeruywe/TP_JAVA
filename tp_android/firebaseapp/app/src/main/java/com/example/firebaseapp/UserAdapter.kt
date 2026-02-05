package com.example.firebaseapp
class UserAdapter(
    private val list: ArrayList<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nom: TextView = v.findViewById(R.id.tvNom)
        val email: TextView = v.findViewById(R.id.tvEmail)
        val tel: TextView = v.findViewById(R.id.tvTel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val u = list[position]
        holder.nom.text = u.nom
        holder.email.text = u.email
        holder.tel.text = u.tel

        holder.itemView.setOnClickListener {
            onClick(u)
        }
    }

    override fun getItemCount() = list.size
}