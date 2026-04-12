package com.example.quickexam.utils;

import com.example.quickexam.http.model.dataGroup.DataResponse;
import com.example.quickexam.repository.DataRepository;
import com.example.quickexam.session.UserSession;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TransmitData {

    public static void transmit(String maxAlcohol, String blood_dia, String blood_sys,
                                String maxTemper, String fag, String ppg, String spo2, File ecg, TransmitCallback callback) {

        DataRepository dataRepo = new DataRepository();
        String account = UserSession.getInstance().getCurrentUser().getAccount();

        // 用原子类保证线程安全
        AtomicInteger remaining = new AtomicInteger(5); // 还剩几个请求没完成
        AtomicBoolean allSuccess = new AtomicBoolean(true); // 是否全部成功

        DataRepository.DataCallback cb = new DataRepository.DataCallback() {
            @Override
            public void onSuccess(DataResponse response) {
                // 每完成一个，计数减一，减到0说明全部完成
                if (remaining.decrementAndGet() == 0) {
                    callback.onComplete(allSuccess.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                allSuccess.set(false); // 标记有失败
                t.printStackTrace();
                if (remaining.decrementAndGet() == 0) {
                    callback.onComplete(false);
                }
            }
        };

        dataRepo.submitAlcoholInfo(account, maxAlcohol, cb);
        dataRepo.submitBloodDiaInfo(account, blood_dia, cb);
        dataRepo.submitBloodSysInfo(account, blood_sys, cb);
        dataRepo.submitTemporInfo(account, maxTemper, cb);
        dataRepo.submitECGInfo(account, ecg, cb);
        dataRepo.submitFagInfo(account, fag, cb);
        dataRepo.submitPPGInfo(account, ppg, cb);
        dataRepo.submitSPO2Info(account, spo2, cb);
    }

    public static void transmit(String dataHr,File ecg, TransmitCallback callback) {

        DataRepository dataRepo = new DataRepository();
        String account = UserSession.getInstance().getCurrentUser().getAccount();

        // 用原子类保证线程安全
        AtomicInteger remaining = new AtomicInteger(5); // 还剩几个请求没完成
        AtomicBoolean allSuccess = new AtomicBoolean(true); // 是否全部成功

        DataRepository.DataCallback cb = new DataRepository.DataCallback() {
            @Override
            public void onSuccess(DataResponse response) {
                // 每完成一个，计数减一，减到0说明全部完成
                if (remaining.decrementAndGet() == 0) {
                    callback.onComplete(allSuccess.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                allSuccess.set(false); // 标记有失败
                t.printStackTrace();
                if (remaining.decrementAndGet() == 0) {
                    callback.onComplete(false);
                }
            }
        };

        dataRepo.submitBloodHrInfo(account, dataHr, cb);
        dataRepo.submitECGInfo(account, ecg, cb);
    }

    // 定义回调接口
    public interface TransmitCallback {
        void onComplete(boolean success);
    }
}

