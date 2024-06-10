import { isEqual } from 'date-fns';

export const numberToTwoDigitNumber = (number) => (number < 10 ? '0' : '') + number;

export const numbersToTime = (number) =>
  isStringInput(number) ? number : number.map(numberToTwoDigitNumber).join(':');

export function compareTime([anHour, aMinute], [otherHour, otherMinute]) {
  return anHour < otherHour || (anHour === otherHour && aMinute < otherMinute) ? -1 : 1;
}

export const sortTimes = (times) =>
  times.sort(({ startTime: [hour1, minute1] }, { startTime: [hour2, minute2] }) =>
    compareTime([hour1, minute1], [hour2, minute2])
  );

export const toDate = (date) =>
  isStringInput(date) ? new Date(date) : getDateFromArray(date);

export const byDate = (date) => (slot) => isEqual(toDate(slot.date), date);

export const getLastEndFromCollectionOfSlots = (slots) =>
  slots.length > 0 ? slots.slice(-1)[0].endTime : undefined;

const isStringInput = (input) => typeof input === 'string' || input instanceof String;

const getDateFromArray = ([year, month, day]) => new Date(year, month - 1, day);
