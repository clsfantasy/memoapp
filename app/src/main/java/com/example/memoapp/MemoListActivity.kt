package com.example.memoapp

import MemoAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memoapp.databinding.ActivityMemoListBinding
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import kotlinx.coroutines.launch
import com.google.gson.Gson
import java.io.File
import java.nio.charset.Charset



class MemoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoListBinding
    private lateinit var memoAdapter: MemoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonExport.setOnClickListener {
            exportMemosToJson()
        }
        binding.buttonImport.setOnClickListener {
            importMemosFromJson()
        }

        val db = AppDatabase.getDatabase(this)
        db.memoDao().getAll().observe(this) { memoList ->
            memoAdapter = MemoAdapter(memoList)
            binding.recyclerView.adapter = memoAdapter
        }




        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, MemoDetailActivity::class.java)
            startActivity(intent)
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
            Toast.makeText(this@MemoListActivity, "导出成功：" + file.absolutePath, Toast.LENGTH_LONG).show()
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


