package com.example.memoapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memoapp.databinding.ActivityMemoDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoDetailBinding
    private var memoId: Int = -1 // -1 表示新增

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = AppDatabase.getDatabase(this).memoDao()

        // 获取传递的数据
        memoId = intent.getIntExtra("memo_id", -1)

        if (memoId != -1) {
            // 编辑模式：查询 Memo 显示
            CoroutineScope(Dispatchers.IO).launch {
                val memo = dao.getById(memoId)
                runOnUiThread {
                    if (memo != null) {
                        binding.editTitle.setText(memo.title)
                        binding.editContent.setText(memo.content)
                    }
                }
            }
        } else {
            // 新增模式：删除按钮隐藏
            binding.btnDelete.isEnabled = false
        }

        // 保存按钮事件
        binding.btnSave.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()

            if (title.isNotBlank() && content.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (memoId == -1) {
                        // 新增
                        dao.insert(Memo(title = title, content = content))
                    } else {
                        // 修改
                        dao.update(Memo(id = memoId, title = title, content = content))
                    }
                    finish()
                }
            } else {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        // 删除按钮事件
        binding.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dao.deleteById(memoId)
                finish()
            }
        }
    }
}
