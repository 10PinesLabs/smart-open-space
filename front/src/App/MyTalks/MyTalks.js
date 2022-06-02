import React, { useCallback, useState } from 'react';

import { Box, Heading, Layer, Text } from 'grommet';
import PropTypes from 'prop-types';

import {
  createTalkFor,
  nextTalk,
  useGetCurrentUserTalks,
  useGetTalks,
} from '#api/os-client';
import { useQueue } from '#api/sockets-client';
import { identify, register } from '#api/user-client';
import MyProps from '#helpers/MyProps';
import { useUser } from '#helpers/useAuth';
import { RedirectToRoot, usePushToNewTalk, usePushToOpenSpace } from '#helpers/routes';
import ButtonLoading from '#shared/ButtonLoading';
import Detail from '#shared/Detail';
import { TalkIcon, UserIcon } from '#shared/icons';
import MainHeader from '#shared/MainHeader';
import MyForm from '#shared/MyForm';
import MyGrid from '#shared/MyGrid';
import Row from '#shared/Row';
import Spinner, { TinySpinner } from '#shared/Spinner';
import Title from '#shared/Title';

import EmptyTalk from './EmptyTalk';
import TalkView from './Talk';
import Talk from '../model/talk';

const slideDownAnimation = {
  type: 'slideDown',
  delay: 0,
  duration: 1000,
  size: 'large',
};

function talkToModel(talk, queue, slots, openSpace) {
  return new Talk(
    talk.id,
    talk.name,
    talk.description,
    talk.meetingLink,
    talk.speaker,
    talk.track,
    queue,
    slots,
    openSpace
  );
}

const EnqueuedTalkCard = ({ bgColor, children }) => (
  <Row justify="center" margin={{ bottom: 'large' }}>
    <Box
      align="center"
      animation={slideDownAnimation}
      background={bgColor}
      elevation="medium"
      gap="medium"
      margin="none"
      pad="medium"
      round
    >
      {children}
    </Box>
  </Row>
);
EnqueuedTalkCard.propTypes = {
  bgColor: PropTypes.string.isRequired,
  children: MyProps.children.isRequired,
};

const PlaceBox = ({ place }) => (
  <>
    <Box
      border={{
        color: 'dark-2',
        size: 'small',
      }}
      pad="small"
      round
    >
      {`Queda${place > 1 ? 'n' : ''}`}
      <Heading alignSelf="center" margin="none">
        {place}
      </Heading>
    </Box>
    {place === 1 && (
      <Text margin={{ horizontal: 'small', vertical: 'none' }} weight="bold">
        Sos el siguiente!!
      </Text>
    )}
  </>
);
PlaceBox.propTypes = { place: PropTypes.number.isRequired };

const EnqueuedTalkCurrent = ({ description, onFinish, title }) => (
  <EnqueuedTalkCard bgColor="accent-1">
    <Heading margin={{ horizontal: 'medium', vertical: 'none' }}>PASÁ!!</Heading>
    <>
      <Title>{title}</Title>
      <Detail color="dark-2" text={description} truncate />
    </>
    <ButtonLoading color="status-critical" label="Terminé" onClick={onFinish} primary />
  </EnqueuedTalkCard>
);
EnqueuedTalkCurrent.propTypes = {
  description: PropTypes.string,
  onFinish: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
};

const EnqueuedTalk = ({ description, place, title }) => (
  <EnqueuedTalkCard bgColor="accent-4">
    <Text
      textAlign="center"
      margin={{ horizontal: 'small', vertical: 'none' }}
      weight="bold"
    >
      ESPERANDO TURNO
    </Text>
    <PlaceBox place={place} />
    <>
      <Title>{title}</Title>
      <Detail color="dark-2" text={description} truncate />
    </>
  </EnqueuedTalkCard>
);
EnqueuedTalk.propTypes = {
  description: PropTypes.string,
  place: PropTypes.number.isRequired,
  title: PropTypes.string.isRequired,
};

const MyEnqueuedTalk = ({ onFinish, place, ...props }) =>
  place === 0 ? (
    <EnqueuedTalkCurrent onFinish={onFinish} {...props} />
  ) : (
    <EnqueuedTalk place={place} {...props} />
  );
MyEnqueuedTalk.propTypes = {
  description: PropTypes.string,
  onFinish: PropTypes.func.isRequired,
  place: PropTypes.number.isRequired,
  title: PropTypes.string.isRequired,
};

const MyTalks = () => {
  const pushToOS = usePushToOpenSpace();
  const pushToNewTalk = usePushToNewTalk();
  const user = useUser();
  const [showQuerySpeaker, setShowQuerySpeaker] = useState(false);
  const [speaker, setSpeaker] = useState();
  const {
    data: [openSpace, assignedSlots, currentUserTalks = []] = [],
    isPending,
    isRejected,
    reload: reloadMyTalks,
  } = useGetCurrentUserTalks();
  const { data: allTalks, reload: reloadTalks } = useGetTalks();

  const reload = useCallback(() => {
    reloadMyTalks();
    reloadTalks();
  }, [reloadMyTalks, reloadTalks]);

  const queue = useQueue(reload);

  if (isPending) return <Spinner />;
  if (isRejected) return <RedirectToRoot />;

  const currentUserIsOrganizer = openSpace && user && openSpace.organizer.id === user.id;
  const isActiveCallForPapers = openSpace && openSpace.isActiveCallForPapers;
  const isMyTalk = (talk) => currentUserTalks.some((eachTalk) => eachTalk.id === talk.id);
  const myEnqueuedTalk = () => queue.find(isMyTalk);
  const hasAnother = (idTalk) => !!myEnqueuedTalk() && myEnqueuedTalk().id !== idTalk;
  const place = () => queue.findIndex(isMyTalk);

  const onCloseQuerySpeaker = () => {
    setShowQuerySpeaker(false);
    setSpeaker(null);
  };

  const talks = (currentUserIsOrganizer ? allTalks : currentUserTalks)?.map((talk) =>
    talkToModel(talk, queue || [], assignedSlots, openSpace)
  );
  const canAddTalk = openSpace && isActiveCallForPapers && !openSpace.finishedQueue;
  const hasTalks = allTalks && currentUserTalks && talks.length > 0;

  const shouldDisplayTalkForSpeakerButton = currentUserIsOrganizer && canAddTalk;

  const shouldDisplayEmptyTalkButton = !hasTalks && canAddTalk;

  const shouldDisplayAddTalkButton = hasTalks && canAddTalk;

  return (
    <>
      <MainHeader>
        <MainHeader.TitleLink onClick={pushToOS}>{openSpace.name}</MainHeader.TitleLink>
        <MainHeader.SubTitle
          icon={TalkIcon}
          label={currentUserIsOrganizer ? 'GESTIONAR CHARLAS' : 'MIS CHARLAS'}
        />
        <MainHeader.Description description={openSpace.description} />
        <MainHeader.Buttons>
          {shouldDisplayAddTalkButton && (
            <MainHeader.ButtonNew label="Charla" key="newTalk" onClick={pushToNewTalk} />
          )}
          {shouldDisplayTalkForSpeakerButton && (
            <MainHeader.ButtonNew
              color="accent-1"
              label="Charla para Orador"
              key="newTalkSpeaker"
              onClick={() => setShowQuerySpeaker(true)}
            />
          )}
        </MainHeader.Buttons>
      </MainHeader>
      {!queue || (!hasTalks && isPending) ? (
        <Spinner />
      ) : !hasTalks ? (
        shouldDisplayEmptyTalkButton && <EmptyTalk onClick={pushToNewTalk} />
      ) : (
        <>
          {queue.length > 0 && myEnqueuedTalk() && (
            <MyEnqueuedTalk
              description={myEnqueuedTalk().description}
              onFinish={() => nextTalk(openSpace.id)}
              place={place()}
              title={myEnqueuedTalk().name}
            />
          )}
          <MyGrid>
            {talks.map((talk) => (
              <TalkView
                talk={talk}
                activeQueue={openSpace.activeQueue}
                freeSlots={openSpace.freeSlots}
                hasAnother={hasAnother(talk.id)}
                onEnqueue={reload}
                assignableSlots={openSpace.assignableSlots}
                currentUserIsOrganizer={currentUserIsOrganizer}
                key={talk.id}
              />
            ))}
          </MyGrid>
        </>
      )}
      {!isActiveCallForPapers && (
        <Detail text={'La convocatoria a propuestas se encuentra cerrada'} />
      )}
      {showQuerySpeaker && (
        <Layer onEsc={onCloseQuerySpeaker} onClickOutside={onCloseQuerySpeaker}>
          <Box pad="medium">
            <Box margin={{ vertical: 'medium' }}>
              {speaker ? (
                <>
                  <Title level="2" label="Nueva charla" />
                  <Detail text={!speaker.id ? 'Orador no registrado' : speaker.name} />
                </>
              ) : (
                <Title level="2" label="¿Para qué Orador?" />
              )}
            </Box>
            {speaker ? (
              <MyForm
                onSecondary={onCloseQuerySpeaker}
                onSubmit={({ value: { description, name, title } }) =>
                  (speaker.id
                    ? Promise.resolve(speaker.id)
                    : register({ email: speaker, name }).then(({ id: speakerId }) =>
                        Promise.resolve(speakerId)
                      )
                  )
                    .then((speakerId) =>
                      createTalkFor(speakerId, openSpace.id, {
                        name: title,
                        description,
                      })
                    )
                    .then(() => reload())
                    .then(() => {
                      onCloseQuerySpeaker();
                      return Promise.resolve();
                    })
                }
              >
                {!speaker.id && (
                  <MyForm.Text icon={<UserIcon />} label="Nombre del orador" />
                )}
                <MyForm.Text label="Título" name="title" />
                <MyForm.TextArea />
              </MyForm>
            ) : (
              <MyForm
                onSecondary={onCloseQuerySpeaker}
                onSubmit={({ value: { email } }) =>
                  identify(email).then((data) => {
                    setSpeaker(data || email);
                    return data;
                  })
                }
              >
                <MyForm.Email />
              </MyForm>
            )}
          </Box>
        </Layer>
      )}
    </>
  );
};

export default MyTalks;
