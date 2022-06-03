import { Heading } from 'grommet';
import TalksGrid from './TalksGrid';
import React from 'react';
import * as PropTypes from 'prop-types';

export function TrackWithTalks({ track }) {
  return (
    <>
      <Heading color={track.color}> {track.name} </Heading>
      <TalksGrid filterBy={(talk) => talk.track.id === track.id} />
    </>
  );
}

TrackWithTalks.propTypes = {
  track: PropTypes.object.isRequired,
};
