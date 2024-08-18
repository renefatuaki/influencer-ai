import { LocalTime } from '@js-joda/core';
import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

type Time = {
  hour: string;
  minute: string;
  meridiem: string;
};

export function convertToLocalTime(time: Time) {
  let hourInt = parseInt(time.hour, 10);
  const hour = time.meridiem === 'PM' ? hourInt + 12 : hourInt;
  const minute = time.minute.padStart(2, '0');

  return LocalTime.parse(`${hour.toString().padStart(2, '0')}:${minute}`);
}
