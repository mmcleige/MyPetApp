package com.mmcleige.petapplication.utils

import java.util.Calendar

object TimeUtils {

    // 🌟 核心魔法：传入出生时间戳，返回类似 "2岁3个月" 或 "5个月" 的智能字符串
    fun calculateAge(birthDateTimestamp: Long): String {
        val birthCalendar = Calendar.getInstance()
        birthCalendar.timeInMillis = birthDateTimestamp

        val currentCalendar = Calendar.getInstance()

        var years = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        var months = currentCalendar.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)

        // 如果今年的生日还没过，岁数就要减 1，月份要补 12
        if (months < 0 || (months == 0 && currentCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
            years--
            months += 12
        }

        // 修正天数导致的月份未满问题
        if (currentCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH)) {
            months--
            if (months < 0) {
                months = 11
            }
        }

        return when {
            years > 0 && months > 0 -> "${years}岁${months}个月"
            years > 0 && months == 0 -> "${years}岁"
            years == 0 && months > 0 -> "${months}个月"
            else -> "刚出生不久"
        }
    }
}
