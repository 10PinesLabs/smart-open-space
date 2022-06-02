import React from 'react';

import { useGetTalks } from '#api/os-client';
import { RedirectToRoot, usePushToNewTalk } from '#helpers/routes';
import MyGrid from '#shared/MyGrid';
import Spinner from '#shared/Spinner';
import Talk from './Talk';
import EmptyTalk from '../MyTalks/EmptyTalk';

const TalksGrid = ({ isActiveCallForPapers, filterBy = () => {} }) => {
  const { data: talks, isPending, isRejected } = useGetTalks();
  const pushToNewTalk = usePushToNewTalk();
  if (isPending) return <Spinner />;
  if (isRejected) return <RedirectToRoot />;

  let shouldDisplayEmptyTalk = talks.length === 0 && isActiveCallForPapers;
  return shouldDisplayEmptyTalk ? (
    <EmptyTalk onClick={pushToNewTalk} />
  ) : (
    <MyGrid>
      {talks.filter(filterBy).map((talk) => (
        <Talk key={talk.id} talk={talk} />
      ))}
    </MyGrid>
  );
};

export default TalksGrid;
