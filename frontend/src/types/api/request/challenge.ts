import { Week } from '@/types/week';

type ChallengeTypeEnum = 'CUSTOM' | 'WALK' | 'CHECK';

interface PostChallengeAddProps {
  title: string;
  goal: number;
  type: ChallengeTypeEnum;
  alert: boolean;
  hour: number;
  minute: number;
  days: Week[];
}
export type { ChallengeTypeEnum, PostChallengeAddProps };
