package com.example.klassenkassa.activities;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.klassenkassa.R;
import com.example.klassenkassa.Requests.GETRequest;
import com.example.klassenkassa.data.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    boolean isRunning = false;
    private String URL = "http://restapi.eu";
    private String username = "app";
    private String password = "user2020";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Timer", "Start Timer");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("Timer", "Stop Timer");
        stopTimerTask();
        stopSelf();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Notification", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    final Handler handler = new Handler();


    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    createNotification();
                });
            }
        };
    }

    private void createNotification(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

        Log.d("Time", LocalTime.now().format(dtf));
        if(LocalTime.now().format(dtf).equals(LocalTime.of(12,0,0).format(dtf))) {//if current time = 12:00:00
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            List<Category> categories = getCategories();

            if(!categories.isEmpty()) {
                String categoriesTomorrow = "";

                for(Category c : categories){
                    if (c.getDueDate().isEqual(LocalDate.now().plusDays(1))){
                        categoriesTomorrow = categoriesTomorrow + c.getName() + ", ";
                    }
                }
                if(categoriesTomorrow.endsWith(", ")){
                    categoriesTomorrow = categoriesTomorrow.substring(0,categoriesTomorrow.length()-2);
                }

                if(!categoriesTomorrow.equals("")) {
                    Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                            .setContentTitle("Die Zahlung ist Morgen fällig!")
                            .setContentText("Kategorien: " + categoriesTomorrow)
                            .setSmallIcon(R.drawable.icon512)
                            .setContentIntent(contentPendingIntent)
                            .setAutoCancel(true)
                            .build();

                    startForeground(1, notification);
                }
            }
        }
    }



    private List<Category> getCategories(){
        List<Category> categories = new ArrayList<>();


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dtf = dtf.withLocale(Locale.getDefault());

        GETRequest requestGET = new GETRequest(URL + "/getcategory.php?username=" + username + "&password=" + password);

        String response = null;
        try {
            response = requestGET.execute("").get();
            JSONArray jsonArray = new JSONArray(response);
            jsonArray = jsonArray.optJSONArray(0);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                int categoryID = jsonObject.getInt("categoryID");
                String name = jsonObject.getString("name");
                String date = jsonObject.getString("dueDate");
                float cost = (float) jsonObject.getDouble("cost");
                LocalDate dt = LocalDate.parse(date, dtf);

                categories.add(new Category(categoryID, name, dt, cost));
            }
        } catch (ExecutionException e) {
            return new ArrayList<Category>();
        } catch (InterruptedException e) {
            return new ArrayList<Category>();
        } catch (JSONException e) {
            return new ArrayList<Category>();
        }

        return categories;
    }
}
