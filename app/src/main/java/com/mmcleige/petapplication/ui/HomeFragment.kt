package com.mmcleige.petapplication.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mmcleige.petapplication.data.local.AppDatabase
import com.mmcleige.petapplication.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // 1. 监听右下角 "+" 号按钮跳转
        binding.fabAddPet.setOnClickListener {
            val intent = Intent(requireContext(), AddPetActivity::class.java)
            startActivity(intent)
        }

        // 2. 召唤后台打工人，去数据库捞数据！
        loadLatestPet()
    }

    private fun loadLatestPet() {
        // 启动协程去后台查数据库
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())

            // 调用我们之前在 PetDao 里写的 getAllPets() 方法
            // collectLatest 是一个超级魔法：它会一直“盯着”数据库看！
            // 只要数据库里的宠物有变化（比如你刚才存了一只新的），它就会立刻收到通知！
            db.petDao().getAllPets().collectLatest { petList ->

                // 查完数据后，必须回到主线程 (Main) 才能修改界面！
                withContext(Dispatchers.Main) {
                    if (petList.isNotEmpty()) {
                        // 数据库里有宠物！我们取第一只（因为之前在 SQL 里写了倒序，所以第一只就是最新添加的）
                        val latestPet = petList[0]

                        // 把它显示到卡片上
                        binding.tvPetName.text = latestPet.name
                        // 拼接详细信息，比如：中华田园犬  |  3.0岁  |  20.0 kg
                        binding.tvPetDetails.text = "${latestPet.breed}  |  ${latestPet.age}岁  |  ${latestPet.weight} kg"

                        // 让卡片显示出来
                        binding.cardPetInfo.visibility = View.VISIBLE
                    } else {
                        // 数据库里空空如也（比如你刚装完 APP）
                        binding.cardPetInfo.visibility = View.GONE // 把卡片藏起来
                        // (可选) 你以后可以在这里加一个可爱的“还没有宠物哦，快去添加吧”的空状态提示图
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
