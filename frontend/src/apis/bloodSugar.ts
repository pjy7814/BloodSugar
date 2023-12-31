import { API_ENDPOINT } from '@env';
import { store, RootState } from '@/redux/store/storeConfig';
import {
  BloodSugarWriteData,
  periodBloodSugarProps,
  BloodSugarApiResponse,
} from '@/types/api/request/bloodSugar';
import { fetchWithAuth } from '@/utils';

export async function periodBloodSugar({
  nickname,
  startDate,
  endDate,
  page,
}: periodBloodSugarProps): Promise<BloodSugarApiResponse> {
  const state: RootState = store.getState();
  const { accessToken } = state.user;

  const startMonth = (startDate.getMonth() + 1).toString().padStart(2, '0');
  const startDay = startDate.getDate().toString().padStart(2, '0');

  const endMonth = (endDate.getMonth() + 1).toString().padStart(2, '0');
  const endDay = endDate.getDate().toString().padStart(2, '0');

  const start = `${startDate.getFullYear()}-${startMonth}-${startDay}`;
  const end = `${endDate.getFullYear()}-${endMonth}-${endDay}`;

  const response = await fetch(
    `${API_ENDPOINT}/bloodsugar/period/${nickname}/${start}/${end}/${page}`,
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    }
  );

  if (!response.ok) {
    throw new Error('Network response was not ok');
  }

  const data = await response.json();
  return data;
}

export const saveBloodSugar = (data: BloodSugarWriteData) =>
  fetchWithAuth('/bloodsugar/save', { body: data, method: 'POST' });
