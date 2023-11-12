package kr.co.sugarmanager.business.menu.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.util.ArrayList;

public class MenuSelectDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private Long userPk;
        private Long menuPk;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private boolean success;
        private ReturnResponse response;
        private ErrorResponse error;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ReturnResponse {
        private Long menuPk;
        private ArrayList<MenuImage> menuImages;
        private BloodSugar bloodSugar;
        private ArrayList<Food> foods;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MenuImage {
        private Long menuImagePk;
        private String menuImageUrl;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class BloodSugar {
        private Integer beforeLevel;
        private Integer afterLevel;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Food {
        private Long foodPk;
        private String foodName;
        private Float foodCal;
        private Float foodGrams;
        private Float foodCarbohydrate;
        private Float foodProtein;
        private Float foodDietaryFiber;
        private Float foodVitamin;
        private Float foodMineral;
        private Float foodSalt;
        private Float foodSugars;
    }
}
