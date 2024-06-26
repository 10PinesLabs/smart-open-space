import React from 'react';
import MyGrid from '#shared/MyGrid';
import Talk from './Talk';
import { Vote } from './Vote';

const TalksGrid = ({ talks, reloadTalks, activeVoting, showSpeakerName }) => {
  return (
    <MyGrid>
      {talks.map((talk) => (
        <Talk key={talk.id} talk={talk} showSpeakerName={showSpeakerName}>
          <Vote talk={talk} reloadTalks={reloadTalks} activeVoting={activeVoting} />
        </Talk>
      ))}
    </MyGrid>
  );
};

export default TalksGrid;
