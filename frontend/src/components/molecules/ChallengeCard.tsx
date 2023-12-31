import React from 'react';
import { DefaultCard, DefaultText } from '@/styles';
import styled from 'styled-components/native';
import BlackRightArrowButton from '@/assets/icon/BlackRightArrowIcon.svg';
import { rHeight, rWidth } from '@/utils';
import ProgressBar from '../atoms/ProgressBar';
import SubFillButton from '../atoms/SubFillButton';
import extractNumber from '../../utils/number';

const ChallengeCardContainer = styled(DefaultCard)`
  display: flex;
  justify-content: space-between;
  align-items: start;
  padding: ${rHeight(24)}px ${rWidth(24)}px;
`;
const CardTitleWrapper = styled(DefaultText)``;
const LeftNumericWrapper = styled(DefaultText)``;
const RightNumericWrapper = styled(DefaultText)``;
const ChallengeContentBox = styled.View`
  display: flex;
  width: 100%;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
`;
const NumericTextWrapper = styled.View`
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  justify-content: center;
`;

interface ChallengeCardProps {
  title: string;
  leftNumeric: string;
  rightNumeric: string;
  buttonType?: 'view' | 'otherAlarm' | 'record' | 'claim';
  buttonTitle?: string;
  onPress?: () => void;
  onPressButton?: () => void;
}

export default function ChanllengeCard({
  title,
  leftNumeric,
  rightNumeric,
  buttonType,
  buttonTitle,
  onPress,
  onPressButton,
}: ChallengeCardProps) {
  const getCardButtonTitle = () => {
    if (leftNumeric === rightNumeric) return '완료됨';
    return extractNumber(leftNumeric) === extractNumber(rightNumeric) - 1
      ? '완료하기'
      : '횟수 추가';
  };
  return (
    <ChallengeCardContainer size="lg" onPress={onPress}>
      <CardTitleWrapper typography="captionr" color="secondary">
        {title}
      </CardTitleWrapper>
      <ChallengeContentBox>
        <NumericTextWrapper>
          <LeftNumericWrapper typography="h1b" color="black">
            {leftNumeric}
          </LeftNumericWrapper>
          <RightNumericWrapper typography="p1b" color="secondary">
            / {rightNumeric}
          </RightNumericWrapper>
        </NumericTextWrapper>
        {buttonType &&
          (() => {
            switch (buttonType) {
              case 'view':
                return (
                  <BlackRightArrowButton
                    width={rWidth(20)}
                    height={rHeight(20)}
                    onPress={onPressButton}
                  />
                );
              case 'claim':
                return (
                  <SubFillButton
                    bgColor={`${
                      leftNumeric === rightNumeric ? 'secondary' : 'b4'
                    }`}
                    onPress={() => {
                      if (leftNumeric === rightNumeric || !onPressButton)
                        return;
                      onPressButton();
                    }}
                    title={getCardButtonTitle()}
                  />
                );
              case 'otherAlarm':
              case 'record':
                return (
                  <SubFillButton
                    bgColor="b4"
                    title={buttonTitle as string}
                    onPress={onPressButton}
                  />
                );
              default:
                return null;
            }
          })()}
      </ChallengeContentBox>
      <ProgressBar current={leftNumeric} goal={rightNumeric} />
    </ChallengeCardContainer>
  );
}
