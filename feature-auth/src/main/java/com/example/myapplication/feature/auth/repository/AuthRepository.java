package com.example.myapplication.feature.auth.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.myapplication.core.data.db.AppDatabase;
import com.example.myapplication.core.data.entity.UserEntity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepository {
    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AuthRepository(Context context) {
        this.db = AppDatabase.getInstance(context);
    }

    public interface AuthCallback {
        void onResult(UserEntity user);
    }

    public void login(String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            UserEntity user = db.userDao().login(email, password);
            mainHandler.post(() -> callback.onResult(user));
        });
    }

    public void signup(String name, String email, String password, String role, AuthCallback callback) {
        executor.execute(() -> {
            UserEntity user = new UserEntity(name, email, password, role);
            long id = db.userDao().insert(user);
            user.setId(id);
            mainHandler.post(() -> callback.onResult(user));
        });
    }
}
