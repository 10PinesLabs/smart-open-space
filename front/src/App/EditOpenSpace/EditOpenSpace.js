import React from 'react';
import { useHistory } from 'react-router-dom';

import { useGetOpenSpace, updateOS } from '#api/os-client';
import { numbersToTime } from '#helpers/time';
import { useUser } from '#helpers/useAuth';
import { RedirectToRoot } from '#helpers/routes';
import { OpenSpaceForm } from './OpenSpaceForm';
import Spinner from '#shared/Spinner';
import { usePushToOpenSpace } from '#helpers/routes';

const EditOpenSpace = () => {
  const history = useHistory();
  const user = useUser();
  const pushToOpenSpace = usePushToOpenSpace();

  const { data: openSpace, isPending } = useGetOpenSpace();
  if (isPending) {
    return <Spinner />;
  } else {
    openSpace.slots.sort(
      (slotA, slotB) =>
        slotA.startTime[0] - slotB.startTime[0] || slotA.startTime[1] - slotB.startTime[1]
    );
    openSpace.slots.forEach((slot) => {
      slot.startTime = numbersToTime(slot.startTime);
      slot.endTime = numbersToTime(slot.endTime);
    });
  }

  if (!user) return <RedirectToRoot />;

  const onSubmit = ({ value }) => {
    const editedOpenSpace = { ...openSpace, ...value };
    updateOS(openSpace.id, editedOpenSpace).then(pushToOpenSpace);
  };

  return (
    <OpenSpaceForm
      history={history}
      title={'Editar Open Space'}
      onSubmit={onSubmit}
      initialValues={openSpace}
      isNewOpenSpace={false}
    />
  );
};

export default EditOpenSpace;
