package kr.co.sugarmanager.business.menu.controller;

import kr.co.sugarmanager.business.menu.dto.*;
import kr.co.sugarmanager.business.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuController {
    private final MenuService menuService;

    @PostMapping(value = "/save", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MenuSaveDTO.Response> save(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @RequestPart(value = "file", required = false) List<MultipartFile> imageFile,
            @Validated @RequestPart MenuSaveDTO.Request memuDto) {
        log.info("MenuSave - Authorization: {}, menuDto: {}", userPk, memuDto.getFoods());

        return new ResponseEntity<>(menuService.save(userPk, imageFile, memuDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/delete", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE})
    public ResponseEntity<MenuDeleteDTO.Response> delete(
            @RequestHeader("Authorization") String auth,
            @Validated @RequestBody MenuDeleteDTO.Request menuPk) {
        log.info("MenuDelete - Authorization: {}, menuDto: {}", auth, menuPk.getMenuPk());
        Long userPk = 1L;       // TODO: 서버간 통신 필요
        return new ResponseEntity<>(menuService.delete(userPk, menuPk), HttpStatus.OK);
    }

    @GetMapping("/{menuPk}")
    @ResponseBody
    public ResponseEntity<MenuSelectDTO.Response> select(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @PathVariable("menuPk") Long menuPk
    ) {
        log.info("MenuSelect - userPk: {}, menuPk: {}", userPk, menuPk);
        MenuSelectDTO.Request request = MenuSelectDTO.Request.builder()
                .userPk(userPk)
                .menuPk(menuPk)
                .build();
        return new ResponseEntity<>(menuService.select(request), HttpStatus.OK);
    }

    @PostMapping(value = "/edit", produces = APPLICATION_JSON_VALUE, consumes = {APPLICATION_JSON_VALUE, MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MenuEditDTO.Response> edit(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @RequestPart(value = "createdMenuImages", required = false) List<MultipartFile> imageFile,
            @Validated @RequestPart MenuEditDTO.Request request
    ) {
        request.setUserPk(userPk);
        request.setCreatedMenuImages(imageFile);
        log.info("MenuEdit - userPk: {}, request: {}", userPk, request);

        return new ResponseEntity<>(menuService.edit(request), HttpStatus.OK);
    }

    @GetMapping(value = "/{nickname}/{year}/{month}/{day}")
    public ResponseEntity<MenuDayDTO.Response> seletDay(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @PathVariable("nickname") String nickname,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @PathVariable("day") int day) {
        log.info("BloodSugar select - userPk: {}, nickname: {}, date: {}-{}-{}", userPk, nickname, year, month, day);

        return new ResponseEntity<>(menuService.selectDay(userPk, nickname, year, month, day), HttpStatus.OK);
    }

    @GetMapping(value = "/period/{nickname}/{startDate}/{endDate}/{page}")
    public ResponseEntity<MenuPeriodDTO.Response> seletPeriod(
            @RequestHeader("X-Authorization-Id") Long userPk,
            @PathVariable("nickname") String nickname,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate,
            @PathVariable("page") Integer page) {
        log.info("BloodSugar Period Select - userPk: {}, nickname: {}, startDate: {}, endDate: {}, page: {},", userPk, nickname, startDate, endDate, page);

        return new ResponseEntity<>(menuService.selectPeriod(userPk, nickname, startDate, endDate, page), HttpStatus.OK);
    }
}
