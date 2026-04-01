package com.mmcleige.petapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mmcleige.petapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 1. 声明一个 binding 变量（你的智能界面管家）
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. 唤醒管家，让他把 activity_main.xml 里的东西全转换成代码能认识的对象
        binding = ActivityMainBinding.inflate(layoutInflater)

        // 3. 把管家手里的根视图（root）展示到手机屏幕上
        setContentView(binding.root)

        // 4. 让界面适配全面屏（这里用 binding.root 完美替代了原来的 R.id.main）
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
