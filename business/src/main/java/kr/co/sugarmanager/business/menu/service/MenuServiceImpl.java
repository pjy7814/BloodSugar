package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.bloodsugar.dto.BLOODSUGARCATEGORY;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.global.user.repository.UserRepository;
import kr.co.sugarmanager.business.menu.dto.*;
import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import kr.co.sugarmanager.business.menu.entity.FoodImageEntity;
import kr.co.sugarmanager.business.menu.entity.ImageEntity;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.exception.MenuException;
import kr.co.sugarmanager.business.menu.repository.FoodImageRepository;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final FoodImageRepository foodImageRepository;
    private final MenuImageService menuImageService;
    private final BloodSugarRepository bloodSugarRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MenuSaveDTO.Response save(Long userPk, List<MultipartFile> imageFiles, MenuSaveDTO.Request request) {
        if (request.getFoods() == null || request.getFoods().size() == 0) {
            throw new ValidationException(ErrorCode.MISSING_INPUT_VALUE);
        }

        MenuEntity menuEntity = MenuEntity.builder()
                .userPk(userPk)
                .registedAt(request.getRegistedAt())
                .foodList(new ArrayList<>())
                .foodImageList(new ArrayList<>())
                .build();
        MenuEntity menu = menuRepository.save(menuEntity);

        for (FoodDTO food : request.getFoods()) {
            FoodEntity foodEntity = new FoodEntity(food);
            foodEntity.setMenuEntity(menu);
//            foodEntity.setMenuEntity(menu);
            menuEntity.addFoodEntity(foodEntity);
            foodRepository.save(foodEntity);
        }

        menuImageService.saveImage(menu.getMenuPk(), ImageTypeEnum.FOOD, imageFiles);

        return MenuSaveDTO.Response
                .builder()
                .success(true)
                .build();
    }

    @Transactional
    @Override
    public MenuDeleteDTO.Response delete(Long userPk, MenuDeleteDTO.Request request) {
        Optional<MenuEntity> menu = menuRepository.findByMenuPkAndUserPk(Long.valueOf(request.getMenuPk()), userPk);
        if (!menu.isPresent()) throw new MenuException(ErrorCode.HANDLE_ACCESS_DENIED);

        menuRepository.delete(menu.get());

        // TODO: delete images
        return MenuDeleteDTO.Response
                .builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuSelectDTO.Response select(MenuSelectDTO.Request request) {
        Long userPk = request.getUserPk();
        Long menuPk = request.getMenuPk();

        // TODO: 내 그룹원이 아닌 다른 사람의 메뉴 조회 할 경우(403 Forbidden) 체크 및 처리 필요
        MenuEntity menu = menuRepository.findByMenuPkAndUserPk(menuPk, userPk)
                .orElseThrow(() -> new MenuException(ErrorCode.MENU_NOT_FOUND_ERROR));

        List<FoodImageEntity> foodImages = menu.getFoodImageList();
        List<MenuSelectDTO.MenuImage> repFoodImages = (foodImages == null) ? new ArrayList<>() : foodImages.stream()
                .map(foodImage -> MenuSelectDTO.MenuImage.builder()
                        .menuImagePk(foodImage.getFoodImagePk())
                        .menuImageUrl(foodImage.getImage().getImageUrl())
                        .build()
                ).toList();

        LocalDateTime registedAt = menu.getRegistedAt();
        LocalDateTime threeHoursBefore = registedAt.minusHours(3);
        LocalDateTime threeHoursAfter = registedAt.plusHours(3);

        BloodSugarEntity beforeBloodSuger = bloodSugarRepository.findOneByUserPkAndCategoryAndRegistedAt(userPk,
                        BLOODSUGARCATEGORY.BEFORE.name(),
                        threeHoursBefore,
                        registedAt)
                .orElse(null);
        BloodSugarEntity afterBloodSuger = bloodSugarRepository.findOneByUserPkAndCategoryAndRegistedAt(userPk,
                        BLOODSUGARCATEGORY.AFTER.name(),
                        registedAt,
                        threeHoursAfter)
                .orElse(null);
        MenuSelectDTO.BloodSugar repBloodSugar = MenuSelectDTO.BloodSugar.builder()
                .beforeLevel((beforeBloodSuger != null) ? beforeBloodSuger.getLevel() : null)
                .afterLevel((afterBloodSuger != null) ? afterBloodSuger.getLevel() : null)
                .build();

        List<FoodEntity> foods = menu.getFoodList();
        List<MenuSelectDTO.Food> repFoods = (foods == null) ? new ArrayList<>() : foods.stream().map(food -> MenuSelectDTO.Food.builder()
                .foodPk(food.getFoodPk())
                .foodName(food.getFoodName())
                .foodCal(food.getFoodCal())
                .foodGrams(food.getFoodGrams())
                .foodCarbohydrate(food.getFoodCarbohydrate())
                .foodProtein(food.getFoodProtein())
                .foodDietaryFiber(food.getFoodDietaryFiber())
                .foodVitamin(food.getFoodVitamin())
                .foodFat(food.getFoodFat())
                .foodSalt(food.getFoodSalt())
                .foodSugars(food.getFoodSugars())
                .build()
        ).toList();

        MenuSelectDTO.ReturnResponse returnResponse = MenuSelectDTO.ReturnResponse.builder()
                .menuPk(menuPk)
                .registedAt(registedAt)
                .menuImages(repFoodImages)
                .bloodSugar(repBloodSugar)
                .foods(repFoods)
                .build();

        return MenuSelectDTO.Response
                .builder()
                .success(true)
                .response(returnResponse)
                .build();
    }

    @Override
    @Transactional
    public MenuEditDTO.Response edit(MenuEditDTO.Request request) {
        Long userPk = request.getUserPk();
        Long menuPk = request.getMenuPk();

        Optional<MenuEntity> menuOptional = menuRepository.findByMenuPkAndUserPk(menuPk, userPk);
        if (!menuOptional.isPresent()) throw new MenuException(ErrorCode.MENU_NOT_FOUND_ERROR);
        MenuEntity menu = menuOptional.get();

        menu.modifyRegistedAt(request.getRegistedAt());

        if(request.getCreatedMenuImages() != null && !request.getCreatedMenuImages().isEmpty()) {
            menuImageService.saveImage(menuPk, ImageTypeEnum.FOOD, request.getCreatedMenuImages());
        }

        if (request.getDeletedMenuImagePks() != null && !request.getDeletedMenuImagePks().isEmpty()) {
            List<String> deletedImagePaths = new ArrayList<>();
            for (Long deletedMenuImagePk : request.getDeletedMenuImagePks()) {
                Optional<FoodImageEntity> optionalFoodImage = foodImageRepository.findByMenuPkAndFoodImagePk(menuPk, deletedMenuImagePk);
                if (!optionalFoodImage.isPresent()) {
                    continue;
                }
                ImageEntity image = optionalFoodImage.get().getImage();
                String imagePath = Paths.get(image.getImagePath(), image.getImageFile()).toString();
                deletedImagePaths.add(imagePath);
            }
            menuImageService.deleteImage(deletedImagePaths);
            foodImageRepository.deleteByFoodImagePkInAndMenuPK(request.getDeletedMenuImagePks(), menuPk);
        }

        if (request.getCreatedFoods() != null && !request.getCreatedFoods().isEmpty()) {
            List<FoodEntity> createdFoodEntities = request.getCreatedFoods().stream()
                    .map(food -> FoodEntity.builder()
                            .menuEntity(menu)
                            .foodName(food.getFoodName())
                            .foodCal(food.getFoodCal())
                            .foodGrams(food.getFoodGrams())
                            .foodCarbohydrate(food.getFoodCarbohydrate())
                            .foodProtein(food.getFoodProtein())
                            .foodDietaryFiber(food.getFoodDietaryFiber())
                            .foodVitamin(food.getFoodVitamin())
                            .foodFat(food.getFoodFat())
                            .foodSalt(food.getFoodSalt())
                            .foodSugars(food.getFoodSugars())
                            .build())
                    .toList();
            foodRepository.saveAll(createdFoodEntities);
        }

        if (request.getUpdatedFoods() != null && !request.getUpdatedFoods().isEmpty()) {
            for (MenuEditDTO.UpdatedFood food : request.getUpdatedFoods()) {
                Optional<FoodEntity> optionalFood = foodRepository.findByMenuPkAndFoodPk(menuPk, food.getFoodPk());
                if (!optionalFood.isPresent()) {
                    continue;
                }
                FoodEntity originFood = optionalFood.get();
                originFood.setFoodPk(food.getFoodPk());
                originFood.setFoodName(food.getFoodName());
                originFood.setFoodCal(food.getFoodCal());
                originFood.setFoodGrams(food.getFoodGrams());
                originFood.setFoodCarbohydrate(food.getFoodCarbohydrate());
                originFood.setFoodProtein(food.getFoodProtein());
                originFood.setFoodDietaryFiber(food.getFoodDietaryFiber());
                originFood.setFoodVitamin(food.getFoodVitamin());
                originFood.setFoodFat(food.getFoodFat());
                originFood.setFoodSalt(food.getFoodSalt());
                originFood.setFoodSugars(food.getFoodSugars());
            }
        }

        if (request.getDeletedFoodPks() != null && !request.getDeletedFoodPks().isEmpty()) {
            foodRepository.deleteByFoodPkInAndMenuPK(request.getDeletedFoodPks(), menuPk);
        }

        return MenuEditDTO.Response
                .builder()
                .success(true)
                .build();
    }


    @Override
    public MenuDayDTO.Response selectDay(Long userPk, String targetUserNickname, int year, int month, int day) {
        Long targetUserPk = isSameGroup(userPk, targetUserNickname);

        List<MenuEntity> menus = menuRepository.findByUserPkAndCreatedAt(targetUserPk, year, month, day);

        MenuDayDTO.Response returnDTO = MenuDayDTO.Response.builder()
                .success(true)
                .response(MenuDayDTO.ReturnResponse.builder().build())
                .error(null)
                .build();
        returnDTO.getResponse().setMenuPreviews(new ArrayList<>());
        Double daySugars = null;
        for (MenuEntity menu : menus) {
            MenuDayDTO.MenuPreview menuPreview = new MenuDayDTO.MenuPreview();
            menuPreview.setMenuPk(menu.getMenuPk());
            menuPreview.setCreatedAt(menu.getCreatedAt());

            for (FoodEntity food : menu.getFoodList()) {
                menuPreview.setFoodCal((menuPreview.getFoodCal() == null) ? food.getFoodCal() : menuPreview.getMenuPk() + food.getFoodCal());
                menuPreview.setFoodSugars((menuPreview.getFoodSugars() == null) ? food.getFoodSugars() : menuPreview.getFoodSugars() + food.getFoodSugars());
                menuPreview.setFoodProtein((menuPreview.getFoodProtein() == null) ? food.getFoodProtein() : menuPreview.getFoodProtein() + food.getFoodProtein());
                menuPreview.setFoodCarbohydrate((menuPreview.getFoodCarbohydrate() == null) ? food.getFoodCarbohydrate() : menuPreview.getFoodCarbohydrate() + food.getFoodCarbohydrate());
                menuPreview.setFoodFat((menuPreview.getFoodFat() == null) ? food.getFoodFat() : menuPreview.getFoodFat() + food.getFoodFat());
            }

            if (menuPreview.getFoodSugars() != null) {
                daySugars = (daySugars == null) ? (double) menuPreview.getFoodSugars() : daySugars + menuPreview.getFoodSugars();
            }
            returnDTO.getResponse().addList(menuPreview);
        }

        returnDTO.getResponse().setDaySugars(daySugars);
        return returnDTO;
    }

    @Override
    public MenuPeriodDTO.Response selectPeriod(Long userPk, String targetUserNickname, String startDate, String endDate, int page) {
        Long targetUserPk = isSameGroup(userPk, targetUserNickname);

        PageRequest pageRequest = PageRequest.of(page, 30);
        LocalDateTime startLocalDate = convertStringToLocalDateTime(startDate);
        LocalDateTime endLocalDate = convertStringToLocalDateTime(endDate).plusDays(1L);

        if (startLocalDate.isAfter(endLocalDate)) {
            throw new MenuException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return MenuPeriodDTO.Response.builder()
                .success(true)
                .response(menuRepository.findByPeriod(targetUserPk, startLocalDate, endLocalDate, pageRequest).getContent())
                .error(null)
                .build();
    }

    private Long isSameGroup(Long userPk, String targetUserNickname) {
        Long targetUserPk = userRepository.findIdByNickname(targetUserNickname).orElse(null);
        if (targetUserPk == null
                || (!targetUserPk.equals(userPk)
                && !userRepository.inSameGroup(userPk, targetUserNickname))) {
            throw new MenuException(ErrorCode.HANDLE_ACCESS_DENIED);
        }

        return targetUserPk;
    }

    private LocalDateTime convertStringToLocalDateTime(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE).atStartOfDay();
        } catch (RuntimeException e) {
            throw new MenuException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
