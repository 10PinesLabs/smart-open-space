import React from 'react';
import { useHistory } from 'react-router-dom';

import { useGetOpenSpace, updateOS } from '#api/os-client';
import { useUser } from '#helpers/useAuth';
import { RedirectToRoot } from '#helpers/routes';
import { OpenSpaceForm } from './OpenSpaceForm';
import Spinner from '#shared/Spinner';
import { usePushToOpenSpace } from '#helpers/routes';

function formatTime(timeArray) {
  let hour = timeArray[0].toString().padStart(2, '0');
  let minute = timeArray[1].toString().padStart(2, '0');
  return `${hour}:${minute}`;
}

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
      slot.startTime = formatTime(slot.startTime);
      slot.endTime = formatTime(slot.endTime);
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
