package com.mmcleige.petapplication.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mmcleige.petapplication.databinding.ActivityAddPetBinding

class AddPetActivity : AppCompatActivity() {

    // 1. 声明这个页面的专属 ViewBinding 管家
    private lateinit var binding: ActivityAddPetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. 唤醒管家，让他接管 activity_add_pet.xml
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. 全面屏适配：直接用 binding.root，彻底消灭 R.id.main 报错！
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. 为接下来的数据库保存打个底：监听保存按钮的点击
        binding.btnSavePet.setOnClickListener {
            // 测试一下管家能不能抓到你在输入框里打的字
            val petName = binding.etPetName.text.toString()

            if (petName.isEmpty()) {
                // 如果没填名字，就在屏幕底部弹出一个小黑条提示
                Toast.makeText(this, "主子的名字不能为空哦！", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "准备保存：$petName", Toast.LENGTH_SHORT).show()
                // 下一步，我们会在这里把数据存进 Room 数据库！
            }
        }
    }
}
