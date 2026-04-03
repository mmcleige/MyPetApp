package com.mmcleige.petapplication.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.mmcleige.petapplication.data.local.AppDatabase
import com.mmcleige.petapplication.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
