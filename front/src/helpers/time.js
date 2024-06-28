import { isEqual } from 'date-fns';

/**
 * Add 0 padding if is "n < 10" to apply format 'HH'
 **/
export const numberToTwoDigitNumber = (number) => (number < 10 ? '0' : '') + number;

/**
 * get time format 'HH:mm' if is an array, otherwise if is already string skip.
 **/
export const numbersToTime = (number) =>
  isStringInput(number) ? number : number.map(numberToTwoDigitNumber).join(':');

/**
 * compare two times as Array or String with either of two formats: ([HH, mm] | 'HH:mm')
 **/
export const compareTime = (t1, t2) =>
  compareTimeByArray(
    getTimeArrayIf(t1, isStringInput(t1)),
    getTimeArrayIf(t2, isStringInput(t2))
  );

/**
 * compare two times only Array support: (expected format [HH, mm])
 **/
export function compareTimeByArray([anHour, aMinute], [otherHour, otherMinute]) {
  return anHour < otherHour || (anHour === otherHour && aMinute < otherMinute) ? -1 : 1;
}

/**
 * Sort list of objects with startTimes with array of hours and minutes (eg: [{startTime: [HH, mm]}, {startTime: [HH, mm]}, ...])
 **/
export function sortTimesByStartTime(times) {
  return times
    .map(getTimeArray)
    .sort(({ startTime: [hour1, minute1] }, { startTime: [hour2, minute2] }) =>
      compareTime([hour1, minute1], [hour2, minute2])
    );
}

/**
 * Sort list of times with array of hours and minutes (eg: [[HH, mm], [HH, mm], ...])
 **/
export function sortTimes(times) {
  times
    .map(getTimeArray)
    .sort(([hour1, minute1], [hour2, minute2]) =>
      compareTime([hour1, minute1], [hour2, minute2])
    );
}

/**
 * Get new Date from date string valid format or array with the following structure ([year, month-1, day])
 **/
export const toDate = (date) =>
  isStringInput(date) ? getDateFromArray(date.split('-')) : getDateFromArray(date);

export const byDate = (date) => (slot) => isEqual(toDate(slot.date), date);

export const getLastEndFromCollectionOfSlots = (slots) =>
  slots.length > 0 ? slots.slice(-1)[0].endTime : undefined;

const isStringInput = (input) => typeof input === 'string' || input instanceof String;

const getDateFromArray = ([year, month, day]) => new Date(year, month - 1, day);

const getTimeArrayIf = (t, cond) => (cond ? getTimeArray(t) : t);

const getTimeArray = (t) => (isStringInput(t) ? t.split(':') : t);
