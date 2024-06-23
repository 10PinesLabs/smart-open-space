import MainHeader from '#shared/MainHeader';
import { LockIcon, UnlockIcon } from '#shared/icons';
import { showSpeakerName, toggleShowSpeakerName } from '#api/os-client';
import React from 'react';

export const ButtonToToggleShowSpeakerName = ({
  openSpaceID,
  setData,
  showSpeakerName,
  ...props
}) => (
  <MainHeader.Button
    color="accent-4"
    icon={showSpeakerName ? <LockIcon /> : <UnlockIcon />}
    label={showSpeakerName ? 'No Mostrar Speaker' : 'Mostrar Speaker'}
    onClick={() => toggleShowSpeakerName(openSpaceID).then(setData)}
    {...props}
  />
);
