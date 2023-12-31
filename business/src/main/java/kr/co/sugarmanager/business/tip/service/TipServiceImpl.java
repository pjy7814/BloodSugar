package kr.co.sugarmanager.business.tip.service;

import kr.co.sugarmanager.business.tip.dto.TipDTO;
import kr.co.sugarmanager.business.tip.entity.TipEntity;
import kr.co.sugarmanager.business.tip.repository.TipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TipServiceImpl implements TipService{

    private final TipRepository tipRepository;
    @Override
    @Transactional(readOnly = true)
    public TipDTO.Response tipOfTheDay(){

        TipEntity tipEntity = tipRepository.findTipOfTheDay();

        TipDTO.Tip tip = TipDTO.Tip.builder()
                .title(tipEntity.getTitle())
                .content(tipEntity.getContent())
                .build();

        return TipDTO.Response.builder()
                .success(true)
                .response(tip)
                .build();
    }
}
