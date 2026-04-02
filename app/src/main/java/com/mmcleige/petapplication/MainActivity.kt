package com.mmcleige.petapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.mmcleige.petapplication.databinding.ActivityMainBinding
import com.mmcleige.petapplication.ui.ExploreFragment
import com.mmcleige.petapplication.ui.HomeFragment
import com.mmcleige.petapplication.ui.MineFragment
import com.mmcleige.petapplication.ui.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 处理全面屏适配
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. App 刚打开时，默认显示【首页】
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // 2. 监听底部导航栏的点击事件
        binding.bottomNav.setOnItemSelectedListener { item ->
            // 根据点击的按钮 ID，切换到对应的 Fragment
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
                R.id.nav_explore -> replaceFragment(ExploreFragment())
                R.id.nav_mine -> replaceFragment(MineFragment())
            }
            // 返回 true 表示我们消费了这个点击事件，图标会变色
            true
        }
    }

    // 3. 这是一个专门用来切换页面的小工具函数
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // 把新的 Fragment 塞进我们 XML 里画的那个容器里
            .commit()
    }
}
