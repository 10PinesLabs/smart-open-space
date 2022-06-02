import React, { useState } from 'react';
import { Box } from 'grommet';

import { activateQueue, finishQueue, useGetOpenSpace } from '#api/os-client';
import { useQueue } from '#api/sockets-client';
import { useUser } from '#helpers/useAuth';
import {
  RedirectToLoginFromOpenSpace,
  RedirectToRoot,
  usePushToSchedule,
} from '#helpers/routes';
import { ScheduleIcon } from '#shared/icons';
import MainHeader from '#shared/MainHeader';
import Spinner from '#shared/Spinner';
import { ButtonSingIn } from '#shared/ButtonSingIn';
import { ButtonFinishMarketplace } from './buttons/ButtonFinishMarketplace';
import { ButtonProjector } from './buttons/ButtonProjector';
import { ButtonStartMarketplace } from './buttons/ButtonStartMarketplace';
import { ButtonToSwitchCallForPapers } from './buttons/ButtonToSwitchCallForPapers';
import { ButtonMyTalks } from './buttons/ButtonMyTalks';
import { QueryForm } from './QueryForm';
import * as PropTypes from 'prop-types';
import { TrackWithTalks } from './TrackWithTalks';

TrackWithTalks.propTypes = {
  tracks: PropTypes.any,
  activeCallForPapers: PropTypes.any,
  filterBy: PropTypes.func,
};
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
    } = {},
    isPending,
    isRejected,
    setData,
  } = useGetOpenSpace();
  const queue = useQueue();
  const pushToSchedule = usePushToSchedule(id);

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
        <MainHeader.Button
          margin={{ top: 'medium' }}
          color="accent-1"
          icon={<ScheduleIcon />}
          label="Agenda"
          onClick={pushToSchedule}
        />
        <MainHeader.Description description={description} />
        <MainHeader.Tracks tracks={tracks} />
        {finishedQueue && <MainHeader.SubTitle label="Marketplace finalizado" />}
        <MainHeader.Buttons>
          {amTheOrganizer && (
            <ButtonToSwitchCallForPapers
              openSpaceID={id}
              setData={setData}
              isActiveCallForPapers={isActiveCallForPapers}
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
        {tracks.map((track) => (
          <TrackWithTalks track={track} activeCallForPapers={isActiveCallForPapers} />
        ))}
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
