package com.mmcleige.petapplication.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.load
import coil.transform.CircleCropTransformation
import com.mmcleige.petapplication.data.local.AppDatabase
import com.mmcleige.petapplication.databinding.FragmentHomeBinding
import com.mmcleige.petapplication.worker.PetReminderWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddPet.setOnClickListener {
            val intent = Intent(requireContext(), AddPetActivity::class.java)
            startActivity(intent)
        }

        // 👇 🌟 核心魔法：点击红色按钮，发布 10 秒后的刺杀...哦不，提醒任务！
        binding.btnTestReminder.setOnClickListener {

            // 1. 写好要塞给打工人的信封内容 (Data)
            val reminderData = Data.Builder()
                .putString("title", "🔔 驱虫大作战！")
                .putString("message", "主子的体内外驱虫时间到啦，为了健康千万别忘哦！")
                .build()

            // 2. 定制任务书 (OneTimeWorkRequest 表示只执行一次)
            val reminderRequest = OneTimeWorkRequestBuilder<PetReminderWorker>()
                .setInitialDelay(10, TimeUnit.SECONDS) // 核心：倒计时 10 秒！
                .setInputData(reminderData) // 把信封塞进去
                .build()

            // 3. 盖章发布！把任务书交给系统最高指挥官 WorkManager
            WorkManager.getInstance(requireContext()).enqueue(reminderRequest)

            // 弹个提示，让你心里有数
            android.widget.Toast.makeText(requireContext(), "闹钟已定好！赶快把 APP 退到手机桌面，等 10 秒钟！", android.widget.Toast.LENGTH_LONG).show()
        }

        loadLatestPet()
    }

    private fun loadLatestPet() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())

            db.petDao().getAllPets().collectLatest { petList ->
                withContext(Dispatchers.Main) {
                    if (petList.isNotEmpty()) {
                        val latestPet = petList[0]

                        binding.tvPetName.text = latestPet.name
                        // 🌟 调用我们写好的 TimeUtils，把时间戳变成 "2个月" 或 "3岁" 的智能文本
                        val intelligentAge = com.mmcleige.petapplication.utils.TimeUtils.calculateAge(latestPet.birthDate)
                        binding.tvPetDetails.text = "${latestPet.breed}  |  $intelligentAge  |  ${latestPet.weight} kg"


                        // 🌟 重点修改：直接把数据库里的地址变成一个真实的 File 文件扔给 Coil
                        if (latestPet.avatarUri != null) {
                            val imageFile = File(latestPet.avatarUri)
                            binding.ivPetAvatar.load(imageFile) {
                                crossfade(true)
                                transformations(CircleCropTransformation()) // 裁成圆形
                            }
                        } else {
                            binding.ivPetAvatar.load(android.R.color.darker_gray) {
                                transformations(CircleCropTransformation())
                            }
                        }

                        binding.cardPetInfo.visibility = View.VISIBLE
                    } else {
                        binding.cardPetInfo.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
