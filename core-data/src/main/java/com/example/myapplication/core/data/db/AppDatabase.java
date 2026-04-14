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
import com.example.myapplication.core.data.dao.TransactionDao;
import com.example.myapplication.core.data.dao.UserDao;
import com.example.myapplication.core.data.entity.ActivityEntity;
import com.example.myapplication.core.data.entity.AnalysisEntity;
import com.example.myapplication.core.data.entity.BatchEntity;
import com.example.myapplication.core.data.entity.BidEntity;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import com.example.myapplication.core.data.entity.MethodEntity;
import com.example.myapplication.core.data.entity.ProductEntity;
import com.example.myapplication.core.data.entity.TransactionEntity;
import com.example.myapplication.core.data.entity.UserEntity;
// Import collaboration entities and DAOs
import com.example.myapplication.core.data.dao.PostDao;
import com.example.myapplication.core.data.dao.CommentDao;
import com.example.myapplication.core.data.dao.LikeDao;
import com.example.myapplication.core.data.entity.PostEntity;
import com.example.myapplication.core.data.entity.CommentEntity;
import com.example.myapplication.core.data.entity.LikeEntity;

import java.util.concurrent.Executors;

@Database(
        entities = {
            AnalysisEntity.class, UserEntity.class, ProductEntity.class, BidEntity.class, 
            ExpenseEntity.class, ActivityEntity.class, MethodEntity.class, BatchEntity.class,
            TransactionEntity.class,
            // Collaboration entities
            PostEntity.class, CommentEntity.class, LikeEntity.class
        },
        version = 29,
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
    public abstract TransactionDao transactionDao();

    // Collaboration DAOs
    public abstract PostDao postDao();
    public abstract CommentDao commentDao();
    public abstract LikeDao likeDao();

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

    static final Migration MIGRATION_26_27 = new Migration(26, 27) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create posts table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "authorId INTEGER NOT NULL, " +
                "caption TEXT, " +
                "imagePath TEXT, " +
                "phase TEXT, " +
                "audience TEXT NOT NULL DEFAULT 'public', " +
                "isVerified INTEGER NOT NULL DEFAULT 0, " +
                "cnnResult TEXT, " +
                "confidence REAL NOT NULL DEFAULT 0, " +
                "likesCount INTEGER NOT NULL DEFAULT 0, " +
                "commentCount INTEGER NOT NULL DEFAULT 0, " +
                "createdAt INTEGER NOT NULL, " +
                "FOREIGN KEY(authorId) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Create comments table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "postId INTEGER NOT NULL, " +
                "userId INTEGER NOT NULL, " +
                "content TEXT NOT NULL, " +
                "createdAt INTEGER NOT NULL, " +
                "likesCount INTEGER NOT NULL DEFAULT 0, " +
                "parentCommentId INTEGER, " +
                "FOREIGN KEY(postId) REFERENCES posts(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(parentCommentId) REFERENCES comments(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Create likes table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS likes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER NOT NULL, " +
                "postId INTEGER, " +
                "commentId INTEGER, " +
                "createdAt INTEGER NOT NULL, " +
                "FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(postId) REFERENCES posts(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(commentId) REFERENCES comments(id) ON DELETE CASCADE, " +
                "UNIQUE(userId, postId), " +
                "UNIQUE(userId, commentId)" +
                ")"
            );
            
            // Create indexes
            database.execSQL("CREATE INDEX IF NOT EXISTS index_posts_authorId ON posts(authorId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_posts_createdAt ON posts(createdAt)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_posts_phase ON posts(phase)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_comments_postId ON comments(postId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_comments_userId ON comments(userId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_comments_parentCommentId ON comments(parentCommentId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_comments_createdAt ON comments(createdAt)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_likes_userId_postId ON likes(userId, postId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_likes_userId_commentId ON likes(userId, commentId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_likes_postId ON likes(postId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_likes_commentId ON likes(commentId)");
        }
    };

    static final Migration MIGRATION_27_28 = new Migration(27, 28) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add bidding fields to products table
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN currentHighestBid REAL DEFAULT 0");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN currentHighestBidderId INTEGER DEFAULT 0");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN listingStatus TEXT DEFAULT 'ACTIVE'");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN finalSalePrice REAL DEFAULT 0");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN winningBuyerId INTEGER DEFAULT 0");
            } catch (Exception ignored) {}
            
            // Create transactions table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "productId INTEGER NOT NULL, " +
                "buyerId INTEGER NOT NULL, " +
                "farmerId INTEGER NOT NULL, " +
                "finalSalePrice REAL NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "totalAmount REAL NOT NULL, " +
                "transactionDate INTEGER NOT NULL, " +
                "transactionStatus TEXT NOT NULL DEFAULT 'COMPLETED', " +
                "paymentStatus TEXT NOT NULL DEFAULT 'PENDING', " +
                "FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(buyerId) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Create indexes for transactions
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_productId ON transactions(productId)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_buyerId ON transactions(buyerId)");
        }
    };

    static final Migration MIGRATION_28_29 = new Migration(28, 29) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add hybrid posting fields to products table
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN postType TEXT DEFAULT 'MANUAL'");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN isVerified INTEGER DEFAULT 0");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE products ADD COLUMN verificationMethod TEXT");
            } catch (Exception ignored) {}
            
            // Add buyer location fields to bids table
            try {
                database.execSQL("ALTER TABLE bids ADD COLUMN buyerLatitude REAL DEFAULT 0");
            } catch (Exception ignored) {}
            try {
                database.execSQL("ALTER TABLE bids ADD COLUMN buyerLongitude REAL DEFAULT 0");
            } catch (Exception ignored) {}
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
                            .addMigrations(MIGRATION_14_15, MIGRATION_15_16, MIGRATION_25_26, MIGRATION_26_27, MIGRATION_27_28, MIGRATION_28_29)
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
