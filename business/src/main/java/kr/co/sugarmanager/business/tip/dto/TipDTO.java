package kr.co.sugarmanager.business.tip.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TipDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private boolean success;
        private String title;
        private String content;
        private String response;
        private ErrorResponse error;
    }
}
