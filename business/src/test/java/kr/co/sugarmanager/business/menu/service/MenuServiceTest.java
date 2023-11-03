package kr.co.sugarmanager.business.menu.service;

import kr.co.sugarmanager.business.menu.dto.FoodDTO;
import kr.co.sugarmanager.business.menu.dto.MenuSaveDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MenuServiceTest {
    @Autowired
    private MenuService menuService;

    @Test
    void 메뉴_저장_성공() throws Exception {
        //given
        FoodDTO foodDTO = FoodDTO.builder()
                .foodName("test")
                .build();
        List<FoodDTO> foodDTOList = new ArrayList<>();
        foodDTOList.add(foodDTO);

        MenuSaveDTO.Request request = MenuSaveDTO.Request.builder()
                .foods(foodDTOList)
                .build();

        //when
        MenuSaveDTO.Response save = menuService.save(1L, null, request);

        //then
        assertEquals(save.isSuccess(), true);
    }
}
