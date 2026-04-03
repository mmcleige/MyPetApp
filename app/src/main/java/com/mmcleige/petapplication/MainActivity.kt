package com.mmcleige.petapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    // 🌟 新增：专门负责向系统讨要“发通知权限”的外交官
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 用户点击了“允许”
        } else {
            // 用户点击了“拒绝”，你可以考虑在这里弹个 Toast 说“没有通知，您可能会错过驱虫哦”
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🌟 APP 一打开，先看保安放不放行 (仅限 Android 13+)
        askNotificationPermission()

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
                R.id.nav_explore -> replaceFragment(ExploreFragment())
                R.id.nav_mine -> replaceFragment(MineFragment())
            }
            true
        }
    }

    private fun askNotificationPermission() {
        // 如果手机系统是 Android 13 (API 33) 或以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 检查是不是还没给权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_DENIED
            ) {
                // 派外交官出马，弹窗问用户要权限！
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
