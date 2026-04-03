package com.mmcleige.petapplication.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mmcleige.petapplication.data.local.AppDatabase
import com.mmcleige.petapplication.data.local.PetEntity
import com.mmcleige.petapplication.databinding.ActivityAddPetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 监听保存按钮点击
        binding.btnSavePet.setOnClickListener {
            savePetData()
        }
    }

    private fun savePetData() {
        // 1. 把输入框里的字抓出来，去掉前后的空格
        val name = binding.etPetName.text.toString().trim()
        val breed = binding.etPetBreed.text.toString().trim()
        val ageStr = binding.etPetAge.text.toString().trim()
        val weightStr = binding.etPetWeight.text.toString().trim()

        // 2. 拦截检查：名字必须填！
        if (name.isEmpty()) {
            Toast.makeText(this, "主子的名字不能为空哦！", Toast.LENGTH_SHORT).show()
            return // 名字没填就直接退出这个函数，不往下走了
        }

        // 3. 把年龄和体重转换成小数 (Double)，如果用户没填或者乱填，就默认当成 0.0
        val age = ageStr.toDoubleOrNull() ?: 0.0
        val weight = weightStr.toDoubleOrNull() ?: 0.0

        // 4. 打包数据：把刚才抓取到的数据，塞进我们建好的 PetEntity 实体类里
        val newPet = PetEntity(
            name = name,
            breed = if (breed.isEmpty()) "未知品种" else breed, // 没填品种就写未知
            age = age,
            weight = weight
        )

        // 5. 召唤后台打工人 (协程) 去存数据库！
        // lifecycleScope.launch 表示启动一个协程，Dispatchers.IO 表示让他去后台干活
        lifecycleScope.launch(Dispatchers.IO) {

            // 获取数据库单例管家
            val db = AppDatabase.getDatabase(applicationContext)
            // 调用 DAO 里的 insertPet 方法，把打包好的 newPet 塞进数据库
            db.petDao().insertPet(newPet)

            // 6. 存完之后，回到主线程 (Dispatchers.Main) 刷新界面
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddPetActivity, "保存成功！", Toast.LENGTH_SHORT).show()
                // finish() 会关掉当前的“添加页面”，自动退回到之前的“首页”
                finish()
            }
        }
    }
}
