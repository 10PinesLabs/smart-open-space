import { Heading } from 'grommet';
import TalksGrid from './TalksGrid';
import React from 'react';

export function TrackWithTalks({ track, activeCallForPapers }) {
  return (
    <>
      <Heading color={track.color}> {track.name} </Heading>
      <TalksGrid
        isActiveCallForPapers={activeCallForPapers}
        filterBy={(talk) => talk.track.id === track.id}
      />
    </>
  );
}
