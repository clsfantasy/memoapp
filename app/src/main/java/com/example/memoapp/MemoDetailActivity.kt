package com.example.memoapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.memoapp.databinding.ActivityMemoDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoDetailBinding
    private var memoId: Int = -1 // -1 表示新增
    private var selectedImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.memoDao()

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
                        binding.editTextCategory.setText(memo.category)
                        selectedImageUri = memo.imageUri
                        if (!selectedImageUri.isNullOrEmpty()) {
                            Glide.with(this@MemoDetailActivity)
                                .load(selectedImageUri)
                                .into(binding.imageView)
                        }
                    }
                }
            }
        } else {
            // 新增模式：删除按钮隐藏
            binding.btnDelete.isEnabled = false
        }

        // 选择图片按钮
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        // 保存按钮事件（只需在保存Memo时加上imageUri = selectedImageUri）
        binding.btnSave.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()
            val category = binding.editTextCategory.text.toString().ifBlank { "默认" }
            val imageUriToSave = selectedImageUri

            if (title.isNotBlank() && content.isNotBlank()) {
                // 用lifecycleScope，保证和Activity生命周期一致
                lifecycleScope.launch(Dispatchers.IO) {
                    if (memoId == -1) {
                        // 新增
                        dao.insert(
                            Memo(
                                title = title,
                                content = content,
                                category = category,
                                timestamp = System.currentTimeMillis(),
                                imageUri = imageUriToSave
                            )
                        )
                    } else {
                        // 修改
                        dao.update(
                            Memo(
                                id = memoId,
                                title = title,
                                content = content,
                                category = category,
                                timestamp = System.currentTimeMillis(),
                                imageUri = imageUriToSave
                            )
                        )
                    }
                    // 回到主线程再finish
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MemoDetailActivity, "保存成功", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show()
            }
        }

        // 删除按钮事件
        binding.btnDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dao.deleteById(memoId)
                runOnUiThread {
                    Toast.makeText(this@MemoDetailActivity, "刪除成功", Toast.LENGTH_SHORT).show()
                }

                finish()
            }
        }



        
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                selectedImageUri = uri.toString()
                Glide.with(this)
                    .load(uri)
                    .into(binding.imageView)
            }
        }
    }
}
