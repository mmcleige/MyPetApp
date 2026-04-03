package com.mmcleige.petapplication.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mmcleige.petapplication.data.local.AppDatabase
import com.mmcleige.petapplication.data.local.WeightRecordEntity
import com.mmcleige.petapplication.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val dateFormatter = SimpleDateFormat("MM-dd", Locale.getDefault())

    // 🌟 记住当前正在看的是哪只宠物！
    private var currentPetId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChartUI()

        binding.btnRecordWeight.setOnClickListener { saveNewWeight() }

        // 隐藏的开发者彩蛋（也升级为带 petId 的版本）
        binding.btnRecordWeight.setOnLongClickListener {
            if (currentPetId != -1) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(requireContext())
                    val currentTime = System.currentTimeMillis()
                    val oneDayMs = 24L * 60 * 60 * 1000
                    for (i in 7 downTo 1) {
                        val pastTime = currentTime - (i * oneDayMs)
                        val randomWeight = 10.0 + (Math.random() * 5)
                        db.petDao().insertWeightRecord(WeightRecordEntity(petId = currentPetId, weight = randomWeight, timestamp = pastTime))
                    }
                    withContext(Dispatchers.Main) { Toast.makeText(requireContext(), "生成模拟数据成功！", Toast.LENGTH_SHORT).show() }
                }
            }
            true
        }

        // 🌟 核心：先去查当前的宠物是谁，然后再查它的体重
        loadCurrentPetAndWeights()
    }

    private fun loadCurrentPetAndWeights() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())

            // 监听最新的一只宠物
            db.petDao().getLatestPetFlow().collectLatest { pet ->
                if (pet != null) {
                    currentPetId = pet.id
                    // 名字同步显示在顶部标题上！
                    withContext(Dispatchers.Main) {
                        binding.tvProfileTitle.text = "${pet.name} 的健康追踪"
                    }

                    // 查到了是谁，就去查它的专属体重记录
                    db.petDao().getWeightRecordsForPet(currentPetId).collectLatest { records ->
                        updateChart(records)
                    }
                }
            }
        }
    }

    private suspend fun updateChart(records: List<WeightRecordEntity>) {
        val entries = ArrayList<Entry>()
        val dateLabels = ArrayList<String>()
        records.forEachIndexed { index, record ->
            entries.add(Entry(index.toFloat(), record.weight.toFloat()))
            dateLabels.add(dateFormatter.format(Date(record.timestamp)))
        }

        withContext(Dispatchers.Main) {
            if (entries.isNotEmpty()) {
                val dataSet = LineDataSet(entries, "体重变化 (kg)")
                dataSet.color = Color.parseColor("#FF6200EE")
                dataSet.setCircleColor(Color.parseColor("#FF6200EE"))
                dataSet.lineWidth = 3f
                dataSet.circleRadius = 5f
                dataSet.valueTextSize = 10f
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataSet.setDrawFilled(true)
                dataSet.fillColor = Color.parseColor("#FF6200EE")
                dataSet.fillAlpha = 50

                val chart = binding.lineChartWeight
                chart.data = LineData(dataSet)
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
                chart.setVisibleXRangeMaximum(6f)
                chart.moveViewToX(entries.size.toFloat() - 1f)
                chart.invalidate()
            }
        }
    }

    private fun setupChartUI() {
        // ... (与之前代码一样，保持不变) ...
        val chart = binding.lineChartWeight
        chart.description.text = ""
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(false)
        chart.setDrawGridBackground(false)
        chart.axisRight.isEnabled = false
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.textColor = Color.DKGRAY
        chart.xAxis.labelRotationAngle = -45f
        chart.xAxis.granularity = 1f
        chart.axisLeft.setDrawGridLines(true)
        chart.axisLeft.textColor = Color.DKGRAY
        chart.setNoDataText("暂无数据，快去首页添加主子吧！")
        chart.setNoDataTextColor(Color.GRAY)
    }

    private fun saveNewWeight() {
        val weightStr = binding.etNewWeight.text.toString().trim()
        if (weightStr.isEmpty() || currentPetId == -1) {
            Toast.makeText(requireContext(), "无法记录", Toast.LENGTH_SHORT).show()
            return
        }

        val weight = weightStr.toDoubleOrNull() ?: 0.0

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())

            // 🌟 终极闭环 1：存入图表专属新记录
            val newRecord = WeightRecordEntity(petId = currentPetId, weight = weight)
            db.petDao().insertWeightRecord(newRecord)

            // 🌟 终极闭环 2：去首页，把首页卡片上的基础体重也改成这个最新数字！！
            db.petDao().updatePetWeight(currentPetId, weight)

            withContext(Dispatchers.Main) {
                binding.etNewWeight.text?.clear()
                Toast.makeText(requireContext(), "打卡成功，首页已同步！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
