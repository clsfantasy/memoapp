import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memoapp.Memo
import com.example.memoapp.MemoDetailActivity
import com.example.memoapp.R

class MemoAdapter(private val memos: List<Memo>) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textContent: TextView = itemView.findViewById(R.id.textContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memos[position]
        holder.textTitle.text = memo.title
        holder.textContent.text = memo.content

        // 点击跳转详情页面
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MemoDetailActivity::class.java)
            intent.putExtra("memo_id", memo.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = memos.size
}
