package com.mmcleige.petapplication.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

// 继承 Worker，说明它是一个可以被系统分配去后台干活的打工人
class PetReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    // 🌟 这是打工人唯一要执行的终极任务：doWork()
    // 哪怕 APP 已经被彻底杀死了，时间一到，系统也会强制执行这段代码！
    override fun doWork(): Result {

        // 1. 获取老板（我们）给它派活时塞在信封里的纸条（提醒标题和内容）
        val title = inputData.getString("title") ?: "宠物健康提醒"
        val message = inputData.getString("message") ?: "您的主子有新的待办事项！"

        // 2. 拉响警报！在手机顶部状态栏弹出通知！
        showNotification(title, message)

        // 3. 告诉系统：报告长官，任务圆满完成！
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        // 召唤 Android 系统的“通知大喇叭”
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "pet_reminder_channel"

        // Android 8.0 以上，所有通知必须归类到一个“频道(Channel)”里
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "宠物健康提醒", // 用户在手机设置里看到的频道名字
                NotificationManager.IMPORTANCE_HIGH // 最高优先级，会发出声音并弹窗显示
            ).apply {
                description = "用于提醒驱虫、疫苗等重要事项"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 精心绘制这张通知卡片的长相
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder) // 使用系统自带的小闹钟图标
            .setContentTitle(title) // 比如：“驱虫提醒”
            .setContentText(message) // 比如：“该给修狗进行体外驱虫啦！”
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 再次强调最高优先级
            .setAutoCancel(true) // 用户点击这条通知后，它会自动消失
            .build()

        // 广播出去！(id 使用当前时间戳，保证如果同时有多条提醒，它们不会互相覆盖)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
