import React from 'react';
import DefaultPressableProps from '@/types/pressable';
import styled from 'styled-components/native';
import { rWidth, rHeight } from '@/utils';

interface PlusButtonProps extends DefaultPressableProps {}

const PlusButtonBox = styled.Pressable`
  width: ${rWidth(40)}px;
  height: ${rHeight(40)}px;
  display: flex;
  justify-content: center;
  align-items: center;
`;
const ButtonTitleWrapper = styled.Text`
  ${({ theme }) => theme.typographys.h3b};
`;

/**
 * @returns +모양의 Button Component
 */

export default function PlusButton({ onPress }: PlusButtonProps) {
  return (
    <PlusButtonBox onPress={onPress}>
      <ButtonTitleWrapper>+</ButtonTitleWrapper>
    </PlusButtonBox>
  );
}
