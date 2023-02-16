import React from 'react';
import HourHeader from '#shared/HourHeader';
import { numbersToTime } from '#helpers/time';
import { OtherSlot } from './OtherSlot';
import { TalkSlot } from './TalkSlot';
import PropTypes from 'prop-types';

export const Slot = ({ slot, talksOf, trackFilter }) => {
  return (
    <React.Fragment>
      <HourHeader hour={numbersToTime(slot.startTime)} />
      {!slot.assignable ? (
        <OtherSlot description={slot.description} />
      ) : (
        <TalkSlot slots={talksOf(slot.id)} trackFilter={trackFilter} />
      )}
    </React.Fragment>
  );
};
Slot.propTypes = {
  slot: PropTypes.object.isRequired,
  talksOf: PropTypes.func.isRequired,
};
