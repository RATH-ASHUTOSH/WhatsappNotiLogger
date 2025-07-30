package com.example.notlisten

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NotifAdapter
    private val notifList = mutableListOf<String>()
    private lateinit var originalList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnEnableNotifAccess)
        val exportBtn = findViewById<Button>(R.id.btnExport)
        val searchBox = findViewById<EditText>(R.id.searchBox)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        btn.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        val prefs = getSharedPreferences("notifs", MODE_PRIVATE)
        val stored = prefs.getString("log", "[]")
        val jsonArray = JSONArray(stored)

        for (i in 0 until jsonArray.length()) {
            notifList.add(jsonArray.getString(i))
        }
        originalList = notifList.toMutableList()

        adapter = NotifAdapter(notifList.toMutableList()) { position ->
            originalList.removeAt(position)
            val newArray = JSONArray()
            originalList.forEach { newArray.put(it) }
            prefs.edit().putString("log", newArray.toString()).apply()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(vh.adapterPosition)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = originalList.filter { it.contains(text.toString(), ignoreCase = true) }
                adapter.filterList(filtered)
            }
        })

        exportBtn.setOnClickListener {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "notif_log.txt")
            file.writeText(originalList.joinToString("\n"))
            Toast.makeText(this, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }
}
