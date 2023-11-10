package kr.co.sugarmanager.business.menu.service;

import jakarta.transaction.Transactional;
import kr.co.sugarmanager.business.bloodsugar.entity.BloodSugarEntity;
import kr.co.sugarmanager.business.bloodsugar.repository.BloodSugarRepository;
import kr.co.sugarmanager.business.global.exception.ErrorCode;
import kr.co.sugarmanager.business.global.exception.ValidationException;
import kr.co.sugarmanager.business.menu.dto.*;
import kr.co.sugarmanager.business.menu.entity.FoodEntity;
import kr.co.sugarmanager.business.menu.entity.FoodImageEntity;
import kr.co.sugarmanager.business.menu.entity.MenuEntity;
import kr.co.sugarmanager.business.menu.exception.MenuException;
import kr.co.sugarmanager.business.menu.repository.FoodRepository;
import kr.co.sugarmanager.business.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final FoodRepository foodRepository;
    private final MenuImageService menuImageService;
    private final BloodSugarRepository bloodSugarRepository;

    @Override
    @Transactional
    public MenuSaveDTO.Response save(Long userPk, List<MultipartFile> imageFiles, MenuSaveDTO.Request request) {
        if (request.getFoods() == null || request.getFoods().size() == 0) {
            throw new ValidationException(ErrorCode.MISSING_INPUT_VALUE);
        }

        MenuEntity menuEntity = MenuEntity.builder()
                .userPk(userPk)
                .foodList(new ArrayList<>())
                .foodImageList(new ArrayList<>())
                .build();
        MenuEntity menu = menuRepository.save(menuEntity);

        for (FoodDTO food : request.getFoods()) {
            FoodEntity foodEntity = new FoodEntity(food);
            foodEntity.setMenuEntity(menu);
            foodEntity.setMenuEntity(menu);
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
        return MenuDeleteDTO.Response
                .builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public MenuSelectDTO.Response select(MenuSelectDTO.Request request) {
        Long userPk = request.getUserPk();
        Long menuPk = request.getMenuPk();

        Optional<MenuEntity> menuOptional = menuRepository.findByMenuPkAndUserPk(menuPk, userPk);
        if (!menuOptional.isPresent()) throw new MenuException(ErrorCode.HANDLE_ACCESS_DENIED);

        MenuEntity menu = menuOptional.get();
        ArrayList<MenuSelectDTO.MenuImage> foodImages = (ArrayList<MenuSelectDTO.MenuImage>) menu.getFoodImageList().stream().map(foodImage -> MenuSelectDTO.MenuImage.builder()
                .menuImagePk(foodImage.getFoodImagePk())
                .menuImageUrl(foodImage.getImage().getImageUrl())
                .build()
        ).toList();

        LocalDateTime createdAt = menu.getCreatedAt();
        LocalDateTime threeHoursBefore = createdAt.minusHours(3);
        LocalDateTime threeHoursAfter = createdAt.plusHours(3);
        BloodSugarEntity beforeBloodSuger = bloodSugarRepository.findOneByUserPkAndCreatedAt(userPk, threeHoursBefore, createdAt).orElse(null);
        BloodSugarEntity afterBloodSuger = bloodSugarRepository.findOneByUserPkAndCreatedAt(userPk, createdAt, threeHoursAfter).orElse(null);
        MenuSelectDTO.BloodSugar bloodSugar = MenuSelectDTO.BloodSugar.builder()
                .beforeLevel(beforeBloodSuger.getLevel())
                .afterLevel(afterBloodSuger.getLevel())
                .build();

        ArrayList<MenuSelectDTO.Food> foods = (ArrayList<MenuSelectDTO.Food>) menu.getFoodList().stream().map(food -> MenuSelectDTO.Food.builder()
                .foodPk(food.getFoodPk())
                .foodName(food.getFoodName())
                .foodCal(food.getFoodCal())
                .foodGrams(food.getFoodGrams())
                .foodCarbohydrate(food.getFoodCarbohydrate())
                .foodProtein(food.getFoodProtein())
                .foodDietaryFiber(food.getFoodDietaryFiber())
                .foodVitamin(food.getFoodVitamin())
                .foodMineral(food.getFoodMineral())
                .foodSalt(food.getFoodSalt())
                .foodSugars(food.getFoodSugars())
                .build()
        ).toList();

        MenuSelectDTO.ReturnResponse returnResponse = MenuSelectDTO.ReturnResponse.builder()
                .menuPk(menuPk)
                .menuImages(foodImages)
                .bloodSugar(bloodSugar)
                .foods(foods)
                .build();

        return MenuSelectDTO.Response
                .builder()
                .success(true)
                .response(returnResponse)
                .build();
    }
}
