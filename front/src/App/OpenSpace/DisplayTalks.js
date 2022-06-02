import { usePushToNewTalk } from '#helpers/routes';
import EmptyTalk from '../MyTalks/EmptyTalk';
import { TrackWithTalks } from './TrackWithTalks';
import TalksGrid from './TalksGrid';
import React from 'react';

export function DisplayTalks({ amountOfTalks, activeCallForPapers, tracks }) {
  const pushToNewTalk = usePushToNewTalk();
  let shouldDisplayEmptyTalk = amountOfTalks === 0 && activeCallForPapers;
  if (shouldDisplayEmptyTalk) return <EmptyTalk onClick={pushToNewTalk} />;
  else {
    let shouldDisplayTrackWithTalks = tracks.length > 0;
    if (shouldDisplayTrackWithTalks)
      return tracks.map((track) => (
        <TrackWithTalks track={track} activeCallForPapers={activeCallForPapers} />
      ));
    else return <TalksGrid />;
  }
}
