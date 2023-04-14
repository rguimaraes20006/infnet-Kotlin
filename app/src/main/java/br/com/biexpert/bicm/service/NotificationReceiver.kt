package br.com.biexpert.bicm.service



import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.biexpert.bicm.R

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Notification", "Notification received")

        if (context != null) {
            val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "RODRIGO")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Tarefa próximo ao vencimento")
                .setContentText("A sua tarefa está vencendo agora!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification.build())
        }
    }
}