package com.mmcleige.petapplication.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.mmcleige.petapplication.data.local.AppDatabase
import com.mmcleige.petapplication.data.local.PetEntity
import com.mmcleige.petapplication.data.local.WeightRecordEntity
import com.mmcleige.petapplication.databinding.ActivityAddPetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AddPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPetBinding
    private var selectedImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivAddAvatar.load(uri) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
    }

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

        binding.ivAddAvatar.setOnClickListener {
            pickMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSavePet.setOnClickListener {
            savePetData()
        }
    }

    // 🌟 核心绝招：搬运工！把系统相册的临时文件，复制到 APP 自己的私有大宅子里
    private fun copyUriToInternalStorage(uri: Uri): String? {
        return try {
            // 打开系统相册的那张图
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            // 给我们自己的图片起个唯一的名字（按时间戳）
            val fileName = "pet_avatar_${System.currentTimeMillis()}.jpg"
            // 在 APP 的私有地盘 (filesDir) 建一个空文件
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            // 开始复制！
            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            // 复制成功，返回这张图片在我们自己地盘里的绝对路径！
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun savePetData() {
        val name = binding.etPetName.text.toString().trim()
        val breed = binding.etPetBreed.text.toString().trim()
        val ageStr = binding.etPetAge.text.toString().trim()
        val weightStr = binding.etPetWeight.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "主子的名字不能为空哦！", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageStr.toDoubleOrNull() ?: 0.0
        val weight = weightStr.toDoubleOrNull() ?: 0.0

        // 让后台协程去干活，因为“复制文件”和“存数据库”都不能卡主界面
        lifecycleScope.launch(Dispatchers.IO) {
            val permanentImagePath = selectedImageUri?.let { copyUriToInternalStorage(it) }

            val newPet = PetEntity(
                name = name,
                breed = if (breed.isEmpty()) "未知品种" else breed,
                age = age,
                weight = weight,
                avatarUri = permanentImagePath
            )

            val db = AppDatabase.getDatabase(applicationContext)

            // 🌟 重点 1：拿到系统分配的专属 ID
            val newPetId = db.petDao().insertPet(newPet)

            // 🌟 重点 2：立刻把填写的“初始体重”作为第一条记录存入图表库！
            val firstWeightRecord = WeightRecordEntity(petId = newPetId.toInt(), weight = weight)
            db.petDao().insertWeightRecord(firstWeightRecord)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddPetActivity, "保存成功！", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }
}
