import React from 'react';
import { useHistory } from 'react-router-dom';

import { createOS } from '#api/os-client';
import { useUser } from '#helpers/useAuth';
import { RedirectToRoot, usePushToRoot } from '#helpers/routes';
import { OpenSpaceForm, splitTime } from './OpenSpaceForm';

const NewOpenSpace = () => {
  const history = useHistory();
  const user = useUser();
  const pushToRoot = usePushToRoot();

  if (!user) return <RedirectToRoot />;

  const onSubmit = ({ value: { dates, name, description, rooms, slots, tracks } }) =>
    createOS({
      dates: dates.map((date) => new Date(date)),
      name,
      description,
      rooms,
      slots: slots.map(({ endTime, startTime, ...rest }) => ({
        ...rest,
        endTime: splitTime(endTime),
        startTime: splitTime(startTime),
      })),
      tracks,
    }).then(pushToRoot);

  return (
    <OpenSpaceForm
      history={history}
      title={'Nuevo Open Space'}
      onSubmit={onSubmit}
      isNewOpenSpace={true}
    />
  );
};

export default NewOpenSpace;
