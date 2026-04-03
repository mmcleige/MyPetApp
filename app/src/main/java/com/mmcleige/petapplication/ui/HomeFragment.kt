package com.mmcleige.petapplication.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mmcleige.petapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // Fragment 使用 ViewBinding 的标准写法 (防内存泄漏)
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 唤醒管家，绑定 fragment_home.xml
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 监听右下角 "+" 号按钮的点击事件
        binding.fabAddPet.setOnClickListener {
            // Intent 是 Android 里的“快递员”，负责在页面间跑腿
            // 它的意思是：从当前页面 (requireContext()) 跳转到 AddPetActivity 页面
            val intent = Intent(requireContext(), AddPetActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 页面销毁时，清空管家，防止内存泄漏（企业级开发规范）
        _binding = null
    }
}
