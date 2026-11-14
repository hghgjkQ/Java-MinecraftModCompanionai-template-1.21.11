package com.companion.skills;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.companion.CompanionAIClient.NRNBTime;

public class GetNRNBCleanTime {
    public static long get(){
        LocalDateTime targetTime = LocalDateTime.of(2026, 5, 1, 6, 2, 4);
        if(NRNBTime!=null){
            targetTime=NRNBTime;
        }
        // 2. 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 3. 计算时间差（对应 Python datetime.now() - target_time）
        Duration duration = Duration.between(targetTime, now);

        // 4. 计算总秒数并取模（对应 Python total_seconds() % 2000）
        long totalSeconds = duration.getSeconds();
        long remainingSeconds = totalSeconds % 2000;

        // 5. 计算下次清理剩余时间（对应 Python 2000 - remaining_seconds）
        return 2000 - remainingSeconds;
    }
}
