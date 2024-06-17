import React, { useState } from 'react';
import { Box, Button } from 'grommet';

import { activateQueue, finishQueue, useGetOpenSpace } from '#api/os-client';
import { useQueue } from '#api/sockets-client';
import { useUser } from '#helpers/useAuth';
import {
  RedirectToLoginFromOpenSpace,
  RedirectToRoot,
  usePushToSchedule,
  usePushToEditOS,
} from '#helpers/routes';
import { EditIcon, ScheduleIcon } from '#shared/icons';
import MainHeader from '#shared/MainHeader';
import Spinner from '#shared/Spinner';
import { ButtonSingIn } from '#shared/ButtonSingIn';
import { ButtonFinishMarketplace } from './buttons/ButtonFinishMarketplace';
import { ButtonProjector } from './buttons/ButtonProjector';
import { ButtonStartMarketplace } from './buttons/ButtonStartMarketplace';
import { ButtonToSwitchCallForPapers } from './buttons/ButtonToSwitchCallForPapers';
import { ButtonToToggleVoting } from './buttons/ButtonToToggleVoting';
import { ButtonMyTalks } from './buttons/ButtonMyTalks';
import { QueryForm } from './QueryForm';
import { DisplayTalks } from './DisplayTalks';

const OpenSpace = () => {
  const user = useUser();
  const [showQuery, setShowQuery] = useState(false);
  const [redirectToLogin, setRedirectToLogin] = useState(false);
  const {
    data: {
      id,
      activeQueue,
      finishedQueue,
      name,
      description,
      tracks,
      organizer,
      pendingQueue,
      isActiveCallForPapers,
      isActiveVoting,
      amountOfTalks,
      dates,
    } = {},
    isPending,
    isRejected,
    setData,
  } = useGetOpenSpace();
  const queue = useQueue();
  const pushToSchedule = usePushToSchedule(id);
  const pushToEditOS = usePushToEditOS(id);

  if (isPending) return <Spinner />;
  if (isRejected) return <RedirectToRoot />;

  const amTheOrganizer = user && organizer.id === user.id;
  const doFinishQueue = () => finishQueue(id).then(setData);
  const marketPlaceButtons = () =>
    (pendingQueue && (
      <ButtonStartMarketplace onClick={() => activateQueue(id).then(setData)} />
    )) ||
    (activeQueue && [
      <ButtonProjector key="projector" />,
      <ButtonFinishMarketplace
        key="finishMarketplace"
        onClick={() => {
          if (queue && queue.length > 0) {
            setShowQuery(true);
            return Promise.resolve();
          }
          return doFinishQueue();
        }}
      />,
    ]);
  return (
    <>
      <MainHeader>
        <MainHeader.Title label={name} />
        {dates && (
          <MainHeader.Button
            margin={{ top: 'medium' }}
            color="accent-1"
            icon={<ScheduleIcon />}
            label="Agenda"
            onClick={pushToSchedule}
          />
        )}
        <MainHeader.Description description={description} />
        <MainHeader.Tracks tracks={tracks} />
        {finishedQueue && <MainHeader.SubTitle label="Marketplace finalizado" />}
        <MainHeader.Buttons>
          {amTheOrganizer && (
            <Button
              icon={<EditIcon />}
              color="accent-4"
              label="Editar"
              onClick={pushToEditOS}
            />
          )}
          {amTheOrganizer && (
            <ButtonToSwitchCallForPapers
              openSpaceID={id}
              setData={setData}
              isActiveCallForPapers={isActiveCallForPapers}
            />
          )}
          {amTheOrganizer && (
            <ButtonToToggleVoting
              openSpaceID={id}
              setData={setData}
              isActiveVoting={isActiveVoting}
            />
          )}
          {user ? (
            <ButtonMyTalks amTheOrganizer={amTheOrganizer} />
          ) : (
            <ButtonSingIn onClick={() => setRedirectToLogin(true)} />
          )}
          {amTheOrganizer && marketPlaceButtons()}
        </MainHeader.Buttons>
      </MainHeader>
      <Box margin={{ bottom: 'medium' }}>
        <DisplayTalks
          amountOfTalks={amountOfTalks}
          activeCallForPapers={isActiveCallForPapers}
          tracks={tracks}
          activeVoting={isActiveVoting}
        />
      </Box>
      {redirectToLogin && <RedirectToLoginFromOpenSpace openSpaceId={id} />}
      {showQuery && (
        <QueryForm
          title="¿Seguro?"
          subTitle={`Queda${queue.length > 1 ? 'n' : ''} ${queue.length} en la cola`}
          onExit={() => setShowQuery(false)}
          onSubmit={doFinishQueue}
        />
      )}
    </>
  );
};

export default OpenSpace;
