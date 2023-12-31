package kr.co.sugarmanager.business.menu.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.util.List;

public class MenuDeleteDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private String menuPk;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private boolean success;
        private String response;
        private ErrorResponse error;
    }
}
