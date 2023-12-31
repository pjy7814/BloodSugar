import React, { useState, useEffect, useCallback } from 'react';
import { ScrollView } from 'react-native';
import styled from 'styled-components/native';
import { periodBloodSugar } from '@/apis/bloodSugar';
import { BloodSugarResponseData } from '@/types/api/request/bloodSugar';
import { useSelector } from 'react-redux';
import { selectNavigation } from '@/redux/slice/navigationSlice';
import useRouter from '@/hooks/useRouter';
import BloodSugarContentCard from '@/components/organisms/BloodSugarContentCard';
import DatePickerController from '@/components/organisms/DatePickerController';
import MainFillButton from '@/components/atoms/MainFillButton';
import { rWidth } from '@/utils';

const BloodSugarContainer = styled.View`
  height: 100%;
  width: 100%;
  justify-content: flex-start;
  align-items: center;
  padding-top: 10%;
`;

const DatePickerControllerWrapper = styled.View`
  width: ${rWidth(320)}px;
`;

const BloodSugarContentCardWrapper = styled.View`
  width: ${rWidth(320)}px;
  padding-top: ${rWidth(20)}px;
`;

const FillButtonWrapper = styled.View`
  width: ${rWidth(320)}px;
  position: absolute;
  bottom: 52px;
  width: 100%;
  align-items: center;
`;

export default function BloodSugarScreen() {
  const router = useRouter();
  const [startDate, setStartDate] = useState(new Date());
  const [endDate, setEndDate] = useState(new Date());
  const { nickname } = useSelector(selectNavigation);
  const [bloodSugarData, setBloodSugarData] = useState<
    BloodSugarResponseData[]
  >([]);
  const [page, setPage] = useState(0);
  const [isTop, setIsTop] = useState(true);
  const [isEnd, setIsEnd] = useState(false);

  const setStartDateSafe = (date: Date) => {
    if (date > endDate) {
      setStartDate(endDate);
    } else {
      setStartDate(date);
    }
  };

  const setEndDateSafe = (date: Date) => {
    const now = new Date();
    if (date > now) {
      setEndDate(now);
    } else if (date < startDate) {
      setEndDate(startDate);
    } else {
      setEndDate(date);
    }
  };

  const fetchBloodSugarData = useCallback(async () => {
    const data = await periodBloodSugar({
      nickname,
      startDate,
      endDate,
      page,
    });
    if (data.response.length === 0) {
      setIsEnd(true);
    } else {
      setBloodSugarData((prevData) => [...prevData, ...data.response]);
    }
  }, [nickname, startDate, endDate, page]);

  useEffect(() => {
    setBloodSugarData([]);
    setPage(0);
    setIsEnd(false);
  }, [nickname, startDate, endDate]);

  useEffect(() => {
    fetchBloodSugarData();
  }, [fetchBloodSugarData]);

  const handleScroll = (event: any) => {
    const { nativeEvent } = event;
    const { contentOffset, layoutMeasurement, contentSize } = nativeEvent;
    const isScrolledToTop = contentOffset.y === 0;
    const isScrolledToBottom =
      Math.round(contentOffset.y + layoutMeasurement.height) >=
      Math.round(contentSize.height);
    setIsTop(isScrolledToTop);
    if (isScrolledToBottom && !isEnd) {
      setPage((prevPage) => prevPage + 1);
    }
  };

  return (
    <BloodSugarContainer>
      <ScrollView onScroll={handleScroll}>
        <DatePickerControllerWrapper>
          <DatePickerController
            startDate={startDate}
            setStartDate={setStartDateSafe}
            endDate={endDate}
            setEndDate={setEndDateSafe}
          />
        </DatePickerControllerWrapper>
        {bloodSugarData.map(
          (item) =>
            item.bloodSugarBefore != null &&
            item.bloodSugarAfter != null && (
              <BloodSugarContentCardWrapper>
                <BloodSugarContentCard
                  key={item.time}
                  date={(() => {
                    const date = new Date(item.time);
                    const month = date.getMonth() + 1;
                    const day = date.getDate();
                    return `${month.toString().padStart(2, '0')}/${day
                      .toString()
                      .padStart(2, '0')}`;
                  })()}
                  count={item.count}
                  beforeNum={Math.round(item.bloodSugarBefore)}
                  beforeType={item.bloodSugarBeforeStatus}
                  afterNum={Math.round(item.bloodSugarAfter)}
                  afterType={item.bloodSugarAfterStatus}
                />
              </BloodSugarContentCardWrapper>
            )
        )}
      </ScrollView>
      {isTop && (
        <FillButtonWrapper>
          <MainFillButton
            bgColor="b4"
            title="혈당 등록하기"
            onPress={() => {
              router.navigate('BloodSugarWrite');
            }}
          />
        </FillButtonWrapper>
      )}
    </BloodSugarContainer>
  );
}
