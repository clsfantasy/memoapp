package com.example.memoapp

import MemoAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memoapp.databinding.ActivityMemoListBinding
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File
import java.nio.charset.Charset


class MemoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoListBinding
    private lateinit var memoAdapter: MemoAdapter
    private var allMemos: List<Memo> = emptyList()
    private var categories: List<String> = listOf("全部")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        memoAdapter = MemoAdapter(emptyList())
        binding.recyclerView.adapter = memoAdapter

        binding.buttonExport.setOnClickListener {
            exportMemosToJson()
        }
        binding.buttonImport.setOnClickListener {
            importMemosFromJson()
        }

        val db = AppDatabase.getDatabase(this)
        db.memoDao().getAll().observe(this) { memoList ->
            allMemos = memoList
            // 获取当前排序选项
            val sortIndex = binding.spinnerSort.selectedItemPosition
            val sortedMemos = when (sortIndex) {
                0 -> allMemos.sortedByDescending { it.timestamp } // 时间降序
                1 -> allMemos.sortedBy { it.timestamp }           // 时间升序
                else -> allMemos
            }
            memoAdapter.setData(sortedMemos)
            // 刷新分类Spinner的数据
            categories = listOf("全部") + allMemos.map { it.category }.distinct()
            val categoryAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            binding.spinnerCategory.adapter = categoryAdapter
            binding.spinnerCategory.setSelection(0)
        }

        // 搜索框监听
        binding.editTextSearch.addTextChangedListener { text ->
            val keyword = text.toString()
            val filtered = if (keyword.isBlank()) {
                allMemos
            } else {
                allMemos.filter {
                    it.title.contains(keyword, ignoreCase = true) ||
                            it.content.contains(keyword, ignoreCase = true)
                }
            }
            memoAdapter.setData(filtered)
        }


        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, MemoDetailActivity::class.java)
            startActivity(intent)
        }

        // 设置排序Spinner的数据
        val sortOptions = listOf("时间降序", "时间升序")
        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        binding.spinnerSort.adapter = sortAdapter

        // 监听分类Spinner的选择变化
        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCategory = categories.getOrNull(position) ?: "全部"
                    val filteredMemos = if (selectedCategory == "全部") {
                        allMemos
                    } else {
                        allMemos.filter { it.category == selectedCategory }
                    }
                    memoAdapter.setData(filteredMemos)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // 监听排序Spinner的选择变化
        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val sortedMemos = when (sortOptions[position]) {
                    "时间降序" -> allMemos.sortedByDescending { it.timestamp }
                    "时间升序" -> allMemos.sortedBy { it.timestamp }
                    else -> allMemos
                }
                memoAdapter.setData(sortedMemos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 导出Memo为JSON
    private fun exportMemosToJson() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.memoDao()
        lifecycleScope.launch {
            val memos = dao.getAllSuspend()
            val json = Gson().toJson(memos)
            val file = File(getExternalFilesDir(null), "memos_backup.json")
            file.writeText(json, Charset.forName("UTF-8"))
            Toast.makeText(
                this@MemoListActivity,
                "导出成功：" + file.absolutePath,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // 导入Memo从JSON
    private fun importMemosFromJson() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.memoDao()
        lifecycleScope.launch {
            val file = File(getExternalFilesDir(null), "memos_backup.json")
            if (!file.exists()) {
                Toast.makeText(this@MemoListActivity, "未找到备份文件", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val json = file.readText(Charset.forName("UTF-8"))
            val type = object : TypeToken<List<Memo>>() {}.type
            val memos: List<Memo> = Gson().fromJson(json, type)
            memos.forEach { dao.insert(it.copy(id = 0)) } // id=0让Room自增
            Toast.makeText(this@MemoListActivity, "导入成功", Toast.LENGTH_SHORT).show()
        }
    }


}


