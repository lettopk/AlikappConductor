package com.Alikapp.alikappconductor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class notifyFirebase extends FirebaseMessagingService {
public static String tokeng="";


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);// s es el token
        Log.e("token", "mi token es: " + s);
        tokeng=s;
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() >0){
            String titulo = remoteMessage.getData().get("titulo");
            String detalle = remoteMessage.getData().get("detalle");
            String info = remoteMessage.getData().get("info");

            if (info !=null) {
                if (info.equals("servicio")) {
                    mayorqueoreo(titulo, detalle);
                }
                if (info.equals("chat")){
                    mayorqueorochat(titulo, detalle);
                }
                if(info.equals("finalizado")){
                    notifyFinalizado(titulo, detalle);
                }
                if(info.equals("cancelado")){
                    notifyCancelado(titulo, detalle);
                }
            }

        }
    }

    private void notifyCancelado(String titulo, String detalle) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {0, 100, 100, 100};
        vibrator.vibrate(pattern, -1);
        String id = "mensaje";
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id,"nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(detalle)
                .setContentIntent(clicknoti())
                .setContentInfo("nuevo")
                .setVibrate(new long[]{0, 1000, 500, 1000});

        Random random =new Random();
        int idnotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idnotify,builder.build());
    }

    private void notifyFinalizado(String titulo, String detalle) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {0, 100, 100, 100};
        vibrator.vibrate(pattern, -1);
        String id = "mensaje";
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id,"nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(detalle)
                .setContentIntent(clicknoti())
                .setContentInfo("nuevo")
                .setVibrate(new long[]{0, 1000, 500, 1000});

        Random random =new Random();
        int idnotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idnotify,builder.build());
    }

    private void mayorqueorochat(String titulo, String detalle) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {0, 100, 100, 100};
        vibrator.vibrate(pattern, -1);
        String id = "mensaje";
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id,"nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.mipmap.ic_car)
                .setContentText(detalle)
                .setContentIntent(clickchat())
                .setContentInfo("nuevo")
                .setVibrate(new long[]{0, 1000, 500, 1000});

        Random random =new Random();
        int idnotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idnotify,builder.build());

    

    }

    private PendingIntent clickchat() {
        Intent ni = new Intent(getApplicationContext(),Chat.class);
        ni.putExtra("color","rojo");
        ni.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,ni,0);
    }

    private void mayorqueoreo(String titulo, String detalle) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {0, 100, 100, 100};
        vibrator.vibrate(pattern, -1);
        String id = "mensaje";
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id,"nuevo",NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.mipmap.ic_car)
                .setContentText(detalle)
                .setContentIntent(clicknoti())
                .setContentInfo("nuevo");
                //.setVibrate(new long[]{0, 1000, 500, 1000});

        Random random =new Random();
        int idnotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idnotify,builder.build());
    }

    private PendingIntent clicknoti() {
        Intent ni = new Intent(getApplicationContext(), CustomerMapActivity.class);
        ni.putExtra("color", "azul");
        ni.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0, ni, 0);
    }
}

