import { Heading } from 'grommet';
import TalksGrid from './TalksGrid';
import React from 'react';
import * as PropTypes from 'prop-types';

export function TrackWithTalks({ talks, reloadTalks, track, activeVoting }) {
  let byTrack = (talk) => talk.track.id === track.id;
  const talksFromTrack = talks.filter(byTrack);
  return (
    <>
      {talksFromTrack.length > 0 && (
        <>
          <Heading color={track.color}> {track.name} </Heading>
          <TalksGrid
            talks={talksFromTrack}
            reloadTalks={reloadTalks}
            activeVoting={activeVoting}
          />
        </>
      )}
    </>
  );
}

TrackWithTalks.propTypes = {
  track: PropTypes.object.isRequired,
};
