package com.example.myapplication.core.data.db;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.myapplication.core.data.dao.ActivityDao;
import com.example.myapplication.core.data.dao.AnalysisDao;
import com.example.myapplication.core.data.dao.BatchDao;
import com.example.myapplication.core.data.dao.BidDao;
import com.example.myapplication.core.data.dao.ExpenseDao;
import com.example.myapplication.core.data.dao.MethodDao;
import com.example.myapplication.core.data.dao.ProductDao;
import com.example.myapplication.core.data.dao.UserDao;
import com.example.myapplication.core.data.entity.ActivityEntity;
import com.example.myapplication.core.data.entity.AnalysisEntity;
import com.example.myapplication.core.data.entity.BatchEntity;
import com.example.myapplication.core.data.entity.BidEntity;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.example.myapplication.core.data.entity.MethodEntity;
import com.example.myapplication.core.data.entity.ProductEntity;
import com.example.myapplication.core.data.entity.UserEntity;

import java.util.concurrent.Executors;

@Database(
        entities = {AnalysisEntity.class, UserEntity.class, ProductEntity.class, BidEntity.class, 
                    ExpenseEntity.class, ActivityEntity.class, MethodEntity.class, BatchEntity.class},
        version = 26,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AnalysisDao analysisDao();
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract BidDao bidDao();
    public abstract ExpenseDao expenseDao();
    public abstract ActivityDao activityDao();
    public abstract MethodDao methodDao();
    public abstract BatchDao batchDao();

    private static volatile AppDatabase INSTANCE;

    static final Migration MIGRATION_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    static final Migration MIGRATION_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            try { database.execSQL("ALTER TABLE users ADD COLUMN username TEXT"); } catch (Exception ignored) {}
            try { database.execSQL("ALTER TABLE users ADD COLUMN bio TEXT"); } catch (Exception ignored) {}
            try { database.execSQL("ALTER TABLE users ADD COLUMN pronouns TEXT"); } catch (Exception ignored) {}
            try { database.execSQL("ALTER TABLE users ADD COLUMN location TEXT"); } catch (Exception ignored) {}
            try { database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT"); } catch (Exception ignored) {}
            try { database.execSQL("ALTER TABLE users ADD COLUMN specialty TEXT"); } catch (Exception ignored) {}
            try { database.execSQL("ALTER TABLE users ADD COLUMN gender TEXT"); } catch (Exception ignored) {}
        }
    };

    static final Migration MIGRATION_25_26 = new Migration(25, 26) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE batches ADD COLUMN actualYieldKg REAL NOT NULL DEFAULT 0");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gcheck.db"
                            )
                            .addMigrations(MIGRATION_14_15, MIGRATION_15_16, MIGRATION_25_26)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        prePopulateLandPrep(getInstance(context));
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void prePopulateLandPrep(AppDatabase db) {
        String phase = "Land Preparation";
        if (db.activityDao().getActivitiesByPhaseSync(phase).isEmpty()) {
            insertActivityWithMethods(db, phase, "Field Clearing", "Manual (bolo/labor)", "Mechanical (brush cutter)", "Chemical (herbicide)");
            insertActivityWithMethods(db, phase, "Plowing", "Tractor", "Hand Tractor (kuliglig)", "Animal (carabao)");
            insertActivityWithMethods(db, phase, "Harrowing", "Harrow", "Rotavator");
            insertActivityWithMethods(db, phase, "Leveling", "Manual leveling", "Mechanical leveling");
            insertActivityWithMethods(db, phase, "Puddling / Water Preparation", "Irrigation system", "Water pump");
            insertActivityWithMethods(db, phase, "Dike (Pilapil) Repair", "Manual repair", "With tools/materials");
        }
    }

    private static void insertActivityWithMethods(AppDatabase db, String phase, String activityName, String... methods) {
        long activityId = db.activityDao().insert(new ActivityEntity(phase, activityName));
        for (String methodName : methods) {
            db.methodDao().insert(new MethodEntity(activityId, methodName));
        }
        db.methodDao().insert(new MethodEntity(activityId, "Other Method"));
    }
}
