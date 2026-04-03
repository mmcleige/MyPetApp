package com.mmcleige.petapplication.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.datepicker.MaterialDatePicker
import com.mmcleige.petapplication.data.local.AppDatabase
import com.mmcleige.petapplication.data.local.PetEntity
import com.mmcleige.petapplication.data.local.WeightRecordEntity
import com.mmcleige.petapplication.databinding.ActivityAddPetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPetBinding
    private var selectedImageUri: Uri? = null

    // 记录选中的出生时间戳（默认就是今天）
    private var selectedBirthDateTimestamp: Long = System.currentTimeMillis()
    // 记录当前是否是“编辑模式”（如果是编辑，这里存的就是宠物的 ID）
    private var existingPetId: Int? = null
    private var existingAvatarUri: String? = null

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

        // 🌟 酷炫功能：点击出生日期框，弹出高颜值的 Material 日历！
        binding.etPetBirthDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("选择主子的生日")
                .setSelection(selectedBirthDateTimestamp)
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedBirthDateTimestamp = selection
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etPetBirthDate.setText(sdf.format(Date(selection)))
            }
            datePicker.show(supportFragmentManager, "BIRTH_DATE_PICKER")
        }

        binding.btnSavePet.setOnClickListener { savePetData() }

        binding.btnResetData.setOnClickListener { resetAllData() }

        // 🌟 进页面先查户口：如果是已经存在的宠物，就进入“编辑模式”
        checkExistingPet()
    }

    private fun checkExistingPet() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val pet = db.petDao().getLatestPetFlow().firstOrNull() // 只取最新的一条数据看一眼

            withContext(Dispatchers.Main) {
                if (pet != null) {
                    // 进入编辑模式！把旧数据填回框里
                    existingPetId = pet.id
                    existingAvatarUri = pet.avatarUri
                    binding.tvFormTitle.text = "修改资料"
                    binding.btnSavePet.text = "保存修改"
                    binding.btnResetData.visibility = View.VISIBLE // 显示危险的重置按钮

                    binding.etPetName.setText(pet.name)
                    binding.etPetBreed.setText(pet.breed)
                    binding.etPetWeight.setText(pet.weight.toString())

                    selectedBirthDateTimestamp = pet.birthDate
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    binding.etPetBirthDate.setText(sdf.format(Date(pet.birthDate)))

                    if (pet.avatarUri != null) {
                        binding.ivAddAvatar.load(File(pet.avatarUri)) {
                            crossfade(true)
                            transformations(CircleCropTransformation())
                        }
                    }
                }
            }
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "pet_avatar_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun savePetData() {
        val name = binding.etPetName.text.toString().trim()
        val breed = binding.etPetBreed.text.toString().trim()
        val weightStr = binding.etPetWeight.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "主子的名字不能为空哦！", Toast.LENGTH_SHORT).show()
            return
        }
        val weight = weightStr.toDoubleOrNull() ?: 0.0

        lifecycleScope.launch(Dispatchers.IO) {
            // 如果用户重新选了照片，就复制新照片；否则沿用旧照片的地址
            val finalAvatarUri = if (selectedImageUri != null) {
                copyUriToInternalStorage(selectedImageUri!!)
            } else {
                existingAvatarUri
            }

            val petToSave = PetEntity(
                id = existingPetId ?: 0, // 如果是编辑，带着旧ID去存，就会覆盖旧数据！
                name = name,
                breed = if (breed.isEmpty()) "未知品种" else breed,
                birthDate = selectedBirthDateTimestamp,
                weight = weight,
                avatarUri = finalAvatarUri
            )

            val db = AppDatabase.getDatabase(applicationContext)
            val newPetId = db.petDao().insertPet(petToSave)

            // 如果是全新添加的宠物（不是修改），就记录第一笔初始体重
            if (existingPetId == null) {
                db.petDao().insertWeightRecord(WeightRecordEntity(petId = newPetId.toInt(), weight = weight))
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddPetActivity, "保存成功！", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 🌟 一键重置清空数据的绝招
    private fun resetAllData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            db.petDao().deleteAllWeightRecords()
            db.petDao().deleteAllPets()

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddPetActivity, "数据已全部清空，您可以重新开始了！", Toast.LENGTH_LONG).show()
                finish() // 杀回首页
            }
        }
    }
}
