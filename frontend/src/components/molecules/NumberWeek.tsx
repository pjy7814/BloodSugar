/* eslint-disable react/no-array-index-key */
/* eslint-disable no-unused-vars */
import React from 'react';
import styled from 'styled-components/native';
import { rWidth, rHeight } from '@/utils';
import NumberDay from '../atoms/NumberDay';

const NumberWeekWrapper = styled.View`
  width: ${rWidth(320)}px;
  height: ${rHeight(40)}px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
`;

interface NumberWeekProps {
  year: number;
  month: number;
  weekInfo: Array<{
    numberDay: number;
    weekDay: string;
    isMarked: boolean;
  }>;
  onPress: (
    selectedYear: number,
    selectedMonth: number,
    selectedDay: number
  ) => void;
}

export default function NumberWeek({
  year,
  month,
  weekInfo,
  onPress,
}: NumberWeekProps) {
  return (
    <NumberWeekWrapper>
      {weekInfo.map(({ numberDay, isMarked }, idx) => (
        <NumberDay
          key={`${year}_${month}_${numberDay}_${idx}`}
          title={numberDay}
          isMarked={isMarked}
          onPress={() => {
            if (!isMarked) return;
            onPress(year, month, numberDay);
          }}
        />
      ))}
    </NumberWeekWrapper>
  );
}
