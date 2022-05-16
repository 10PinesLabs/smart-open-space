import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { Box, Layer } from 'grommet';

import { activateQueue, finishQueue, useGetOS } from '#api/os-client';
import { useQueue } from '#api/sockets-client';
import MyProps from '#helpers/MyProps';
import useAuth, { useUser } from '#helpers/useAuth';
import { RedirectToRoot, usePushToProjector, usePushToMyTalks } from '#helpers/routes';
import Detail from '#shared/Detail';
import { CartIcon, ScheduleIcon, TalkIcon, UserAddIcon, VideoIcon } from '#shared/icons';
import MainHeader from '#shared/MainHeader';
import MyForm from '#shared/MyForm';
import Spinner from '#shared/Spinner';
import Title from '#shared/Title';

import Schedule from './Schedule';
import TalksGrid from './TalksGrid';

const IdentifyForm = ({ children, onSecondary, onSubmit, title }) => (
  <>
    <Box margin={{ vertical: 'medium' }}>
      <Title level="2" label={title} />
    </Box>
    <MyForm onSecondary={onSecondary} onSubmit={onSubmit}>
      {children}
    </MyForm>
  </>
);
IdentifyForm.propTypes = {
  children: MyProps.children.isRequired,
  onSecondary: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
};

const Identify = ({ onExit }) => {
  const [email, setEmail] = useState();
  const { identify, register } = useAuth();

  const onRegister = ({ value: { name } }) => register({ email, name }).then(onExit);

  const onIdentify = ({ value: { email } }) =>
    identify(email).then((data) => {
      if (data) {
        onExit();
      } else {
        setEmail(email);
      }
      return data;
    });

  return (
    <Layer onEsc={onExit} onClickOutside={onExit}>
      <Box pad="medium">
        {email ? (
          <IdentifyForm onSecondary={onExit} onSubmit={onRegister} title="¿Tu nombre?">
            <MyForm.Text label="Para mostrarlo en tus charlas" />
          </IdentifyForm>
        ) : (
          <IdentifyForm onSecondary={onExit} onSubmit={onIdentify} title="¿Quien sos?">
            <MyForm.Email />
          </IdentifyForm>
        )}
      </Box>
    </Layer>
  );
};
Identify.propTypes = { onExit: PropTypes.func.isRequired };

const QueryForm = ({ title, subTitle, onExit, onSubmit }) => (
  <Layer onEsc={onExit} onClickOutside={onExit}>
    <Box pad="medium">
      <Box margin={{ vertical: 'medium' }}>
        <Title level="2" label={title} />
        <Detail size="large" text={subTitle} textAlign="center" />
      </Box>
      <MyForm
        onSecondary={onExit}
        onSubmit={(data) => {
          onExit();
          return onSubmit(data);
        }}
      />
    </Box>
  </Layer>
);
QueryForm.propTypes = {
  title: PropTypes.string.isRequired,
  subTitle: PropTypes.string.isRequired,
  onExit: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

const ButtonMyTalks = ({ amTheOrganizer }) => (
  <MainHeader.Button
    color="accent-1"
    icon={<TalkIcon />}
    label={amTheOrganizer ? 'Gestionar Charlas' : 'Mis charlas'}
    onClick={usePushToMyTalks()}
  />
);
ButtonMyTalks.propTypes = { amTheOrganizer: PropTypes.bool.isRequired };

const ButtonSingIn = (props) => (
  <MainHeader.Button
    color="accent-3"
    icon={<UserAddIcon />}
    label="Ingresar"
    {...props}
  />
);

const ButtonStartMarketplace = (props) => (
  <MainHeader.ButtonLoading
    color="accent-4"
    icon={<CartIcon />}
    label="Iniciar Marketplace"
    {...props}
  />
);

const ButtonProjector = () => (
  <MainHeader.Button
    color="accent-2"
    icon={<VideoIcon />}
    label="Proyector"
    onClick={usePushToProjector()}
  />
);

const ButtonFinishMarketplace = (props) => (
  <MainHeader.ButtonLoading
    color="neutral-4"
    icon={<CartIcon />}
    label="Finalizar Marketplace"
    {...props}
  />
);

const OpenSpace = () => {
  const user = useUser();
  const [showQuery, setShowQuery] = useState(false);
  const [showIdentify, setShowIdentify] = useState(false);
  const {
    data: {
      id,
      activeQueue,
      finishedQueue,
      name,
      description,
      organizer,
      pendingQueue,
      slots,
    } = {},
    isPending,
    isRejected,
    setData,
  } = useGetOS();
  const queue = useQueue();

  if (isPending) return <Spinner />;
  if (isRejected) return <RedirectToRoot />;

  const amTheOrganizer = user && organizer.id === user.id;
  const doFinishQueue = () => finishQueue(id).then(setData);

  const organizerButtons = () =>
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
        {pendingQueue ? (
          <MainHeader.SubTitle icon={TalkIcon} label="CHARLAS" />
        ) : (
          <MainHeader.SubTitle icon={ScheduleIcon} label="AGENDA" />
        )}
        <MainHeader.Description description={description} />
        {finishedQueue && <MainHeader.SubTitle label="Marketplace finalizado" />}
        <MainHeader.Buttons>
          {user ? (
            <ButtonMyTalks amTheOrganizer={amTheOrganizer} />
          ) : (
            <ButtonSingIn onClick={() => setShowIdentify(true)} />
          )}
          {amTheOrganizer && organizerButtons()}
        </MainHeader.Buttons>
      </MainHeader>
      <Box margin={{ bottom: 'medium' }}>
        {pendingQueue ? <TalksGrid /> : <Schedule slots={slots} />}
      </Box>
      {showIdentify && <Identify onExit={() => setShowIdentify(false)} />}
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
