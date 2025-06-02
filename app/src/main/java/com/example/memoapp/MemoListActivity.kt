package com.example.memoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memoapp.databinding.ActivityMemoListBinding
import kotlinx.coroutines.launch

class MemoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoListBinding
    private lateinit var memoAdapter: MemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val db = AppDatabase.getDatabase(this)
        db.memoDao().getAll().observe(this) { memoList ->
            memoAdapter = MemoAdapter(memoList)
            binding.recyclerView.adapter = memoAdapter
        }

        // 测试：添加一条记录
        lifecycleScope.launch {
            db.memoDao().insert(Memo(title = "测试标题", content = "这是一条测试内容"))
        }
    }
}
