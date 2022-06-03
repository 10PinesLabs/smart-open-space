import React from 'react';

import { useGetTalks } from '#api/os-client';
import { RedirectToRoot } from '#helpers/routes';
import MyGrid from '#shared/MyGrid';
import Spinner from '#shared/Spinner';
import Talk from './Talk';

const noCriteria = () => true;

const TalksGrid = ({ filterBy = noCriteria }) => {
  const { data: talks, isPending, isRejected } = useGetTalks();
  if (isPending) return <Spinner />;
  if (isRejected) return <RedirectToRoot />;

  return (
    <MyGrid>
      {talks.filter(filterBy).map((talk) => (
        <Talk key={talk.id} talk={talk} />
      ))}
    </MyGrid>
  );
};

export default TalksGrid;
