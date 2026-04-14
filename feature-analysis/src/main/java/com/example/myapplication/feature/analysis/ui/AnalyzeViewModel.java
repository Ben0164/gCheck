package com.example.myapplication.feature.analysis.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import com.example.myapplication.core.data.entity.AnalysisEntity;
import com.example.myapplication.feature.analysis.analysis.AnalysisResult;
import com.example.myapplication.feature.analysis.analysis.AnalysisService;
import com.example.myapplication.feature.analysis.analysis.MockAnalysisService;
import com.example.myapplication.feature.analysis.repository.AnalysisRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyzeViewModel extends AndroidViewModel {
    public static class AnalysisUiModel {
        private final long analysisId;
        private final int goodPercentage;
        private final int badPercentage;
        private final String grade;
        private final double price;

        public AnalysisUiModel(long analysisId, int goodPercentage, int badPercentage, String grade, double price) {
            this.analysisId = analysisId;
            this.goodPercentage = goodPercentage;
            this.badPercentage = badPercentage;
            this.grade = grade;
            this.price = price;
        }

        public long getAnalysisId() { return analysisId; }
        public int getGoodPercentage() { return goodPercentage; }
        public int getBadPercentage() { return badPercentage; }
        public String getGrade() { return grade; }
        public double getPrice() { return price; }
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AnalysisService analysisService;
    private final AnalysisRepository analysisRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<AnalysisUiModel> analysisResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AnalyzeViewModel(@NonNull Application application) {
        super(application);
        this.analysisService = new MockAnalysisService();
        this.analysisRepository = new AnalysisRepository(application.getApplicationContext());
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<AnalysisUiModel> getAnalysisResult() { return analysisResult; }
    public LiveData<String> getError() { return error; }

    public void analyze(double moisture) {
        loading.setValue(true);
        error.setValue(null);
        executor.execute(() -> {
            AnalysisResult result = analysisService.analyze(null, moisture);
            AnalysisEntity entity = new AnalysisEntity(
                    moisture,
                    result.getGoodPercentage(),
                    result.getBadPercentage(),
                    result.getGrade(),
                    result.getPrice(),
                    System.currentTimeMillis()
            );
            long id = analysisRepository.insert(entity);
            analysisResult.postValue(new AnalysisUiModel(
                    id,
                    result.getGoodPercentage(),
                    result.getBadPercentage(),
                    result.getGrade(),
                    result.getPrice()
            ));
            loading.postValue(false);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
