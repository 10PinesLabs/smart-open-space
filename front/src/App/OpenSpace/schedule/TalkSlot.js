import { Box } from 'grommet';
import Talks from '../Talks';
import Talk from '../Talk';
import React from 'react';
import PropTypes from 'prop-types';

export const TalkSlot = ({ slots, trackFilter, roomFilter }) =>
  slots.length === 0 ? (
    <Box height="small" />
  ) : (
    <Talks>
      {slots
        .filter(({ talk, room }) => {
          return (
            (trackFilter === 'Todas' || talk.track.name === trackFilter) &&
            (roomFilter === 'Todas' || room.name === roomFilter)
          );
        })
        .map(({ talk, room }) => (
          <Talk key={talk.id} talk={talk} room={room} />
        ))}
    </Talks>
  );
TalkSlot.propTypes = { slots: PropTypes.arrayOf(PropTypes.shape()).isRequired };
