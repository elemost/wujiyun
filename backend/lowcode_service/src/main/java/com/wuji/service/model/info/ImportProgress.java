package com.wuji.service.model.info;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportProgress {

    private Integer addCount = 0;

    private Integer updateCount = 0;

    private Integer totalCount = 0;

    private Integer errorCount = 0;

    private Integer successCount = 0;

    private List<ErrorMessage> errorList = new ArrayList<>();

    @Data
    public static class ErrorMessage {
        private Integer row;

        private String errorMessage;
    }

    // 无参构造
    public ImportProgress() {
    }

    // 有参构造（用于快速初始化）
    public ImportProgress(int addCount, int updateCount, int totalCount) {
        this.addCount = addCount;
        this.updateCount = updateCount;
        this.totalCount = totalCount;
    }

    public ImportProgress(int successCount, int errorCount) {
        this.errorCount = errorCount;
        this.successCount = successCount;
    }

    public void incrementUpdateCount() {
        this.updateCount++;
    }

    public void addErrorMessage(String message, Integer row) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorMessage(message);
        errorMessage.setRow(row);
        this.errorList.add(errorMessage);
        this.errorCount++;
    }

    public void incrementAddCount() {
        this.addCount++;
    }

    public void incrementSuccessCount() {
        this.successCount++;
    }
}
