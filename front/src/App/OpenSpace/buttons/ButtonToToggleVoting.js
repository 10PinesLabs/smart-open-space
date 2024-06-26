import MainHeader from '#shared/MainHeader';
import { LockIcon, UnlockIcon } from '#shared/icons';
import { startCallForPapers, toggleVoting } from '#api/os-client';
import React from 'react';

export const ButtonToToggleVoting = ({
  openSpaceID,
  setData,
  isActiveVoting,
  ...props
}) => (
  <MainHeader.Button
    color="accent-4"
    icon={isActiveVoting ? <LockIcon /> : <UnlockIcon />}
    label={isActiveVoting ? 'Cerrar votación' : 'Abrir votación'}
    onClick={() => toggleVoting(openSpaceID).then(setData)}
    {...props}
  />
);
