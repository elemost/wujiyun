package com.wuji.service.cache;

import com.wuji.service.model.info.ImportProgress;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 导入进度存储工具类，支持区分新增和修改条数，使用ConcurrentHashMap保证线程安全
 */
public class TaskProgressHolder {
    // 存储任务ID -> 导入进度（包含新增、修改、总数）
    private static final Map<String, ImportProgress> PROGRESS_MAP = new ConcurrentHashMap<>();

    /**
     * 初始化任务进度（任务开始时调用，避免空指针）
     *
     * @param taskId 任务ID
     */
    public static void initProgress(String taskId, Integer totalCount) {
        ImportProgress importProgress = new ImportProgress();
        importProgress.setTotalCount(totalCount);
        PROGRESS_MAP.put(taskId, importProgress);
    }

    /**
     * 自增新增条数（推荐使用，简化操作）
     *
     * @param taskId 任务ID
     */
    public static void incrementAddCount(String taskId) {
        ImportProgress progress = PROGRESS_MAP.get(taskId);
        if (progress != null) {
            progress.incrementAddCount();
        }
    }

    public static void incrementSuccessCount(String taskId) {
        ImportProgress progress = PROGRESS_MAP.get(taskId);
        if (progress != null) {
            progress.incrementSuccessCount();
        }
    }

    /**
     * 自增修改条数（推荐使用，简化操作）
     *
     * @param taskId 任务ID
     */
    public static void incrementUpdateCount(String taskId) {
        ImportProgress progress = PROGRESS_MAP.get(taskId);
        if (progress != null) {
            progress.incrementUpdateCount();
        }
    }

    public static void incrementError(String taskId, Integer rowNum, String errorMessage) {
        ImportProgress progress = PROGRESS_MAP.get(taskId);
        if (progress != null) {
            progress.addErrorMessage(errorMessage, rowNum);
        }
    }

    /**
     * 手动更新进度（适用于批量更新场景）
     *
     * @param taskId   任务ID
     * @param progress 新的进度对象
     */
    public static void updateProgress(String taskId, ImportProgress progress) {
        PROGRESS_MAP.put(taskId, progress);
    }

    /**
     * 获取导入进度
     *
     * @param taskId 任务ID
     * @return 导入进度对象，无则返回空的进度对象（避免空指针）
     */
    public static ImportProgress getProgress(String taskId) {
        return PROGRESS_MAP.getOrDefault(taskId, new ImportProgress());
    }

    /**
     * 移除任务进度（导入完成后清理）
     *
     * @param taskId 任务ID
     */
    public static void removeProgress(String taskId) {
        PROGRESS_MAP.remove(taskId);
    }
}
