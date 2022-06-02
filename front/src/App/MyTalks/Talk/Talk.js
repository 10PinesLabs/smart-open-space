import React, { useState } from 'react';

import { Box, Button, Text } from 'grommet';
import PropTypes from 'prop-types';

import { enqueueTalk, exchangeTalk, scheduleTalk } from '#api/os-client';
import { useUser } from '#helpers/useAuth';
import ButtonLoading from '#shared/ButtonLoading';
import Card from '#shared/Card';
import Detail from '#shared/Detail';
import { TransactionIcon, UserIcon } from '#shared/icons';
import Title from '#shared/Title';

import SelectSlot from './SelectSlot';
import { usePushToOpenSpace, usePushToSchedule } from '#helpers/routes';

const Badge = ({ color, text }) => (
  <Box alignSelf="center">
    <Text color={color} weight="bold">
      {text}
    </Text>
  </Box>
);
Badge.propTypes = { color: PropTypes.string, text: PropTypes.string };

const ButtonAction = (props) => (
  <ButtonLoading alignSelf="center" margin={{ top: 'medium' }} {...props} />
);

const Talk = ({
  talk,
  activeQueue,
  assignableSlots,
  freeSlots,
  hasAnother,
  onEnqueue,
  currentUserIsOrganizer,
}) => {
  const pushToSchedule = usePushToSchedule();
  const pushToOpenSpace = usePushToOpenSpace();
  const user = useUser();
  const [openSchedule, setOpenSchedule] = useState(false);
  const [openExchange, setOpenExchange] = useState(false);
  const shouldDisplayScheduleTalkButton = currentUserIsOrganizer || talk.isToSchedule();

  const onSubmitSchedule = ({ value: { time, room } }) =>
    scheduleTalk(talk.id, room.id, time, user.id).then(pushToSchedule);

  const onSubmitExchange = ({ value: { time, room } }) =>
    exchangeTalk(talk.id, room.id, time).then(pushToOpenSpace);

  const color = talk.track
    ? talk.track.color
    : talk.isAssigned()
    ? 'status-ok'
    : `accent-${talk.isToSchedule() ? 3 : talk.isInqueue() ? 2 : 4}`;

  return (
    <Card borderColor={color}>
      <Box>
        <Title>{talk.name}</Title>
        <Detail size="small" text={talk.description} truncate />
        {currentUserIsOrganizer && (
          <>
            <Detail icon={UserIcon} text={talk.speaker.name} />
            <Detail size="small" text={talk.speaker.email} />
          </>
        )}
      </Box>
      {talk.isAssigned() ? (
        <Box direction="row" justify="evenly">
          <Badge color={color} text="Agendada" />
          {currentUserIsOrganizer && (
            <Button
              hoverIndicator
              icon={<TransactionIcon />}
              onClick={() => setOpenExchange(true)}
              plain
            />
          )}
        </Box>
      ) : (
        shouldDisplayScheduleTalkButton && (
          <ButtonAction
            color={color}
            label="Agendar"
            onClick={() => setOpenSchedule(true)}
          />
        )
      )}
      {talk.isInqueue() ? (
        <Badge color={color} text="Esperando turno" />
      ) : (
        talk.canBeQueued() &&
        activeQueue && (
          <ButtonAction
            color={color}
            disabled={!currentUserIsOrganizer && hasAnother}
            label="Encolar"
            onClick={() => enqueueTalk(talk.id).then(onEnqueue)}
          />
        )
      )}
      {openSchedule && freeSlots && (
        <SelectSlot
          freeSlots={freeSlots}
          name={talk.name}
          onExit={() => setOpenSchedule(false)}
          onSubmit={onSubmitSchedule}
          title="Agendate!"
        />
      )}
      {openExchange && (
        <SelectSlot
          freeSlots={assignableSlots}
          name={talk.name}
          onExit={() => setOpenExchange(false)}
          onSubmit={onSubmitExchange}
          title="Mover a:"
        />
      )}
    </Card>
  );
};
Talk.propTypes = {
  activeQueue: PropTypes.bool.isRequired,
  assignableSlots: PropTypes.arrayOf(PropTypes.shape({}).isRequired).isRequired,
  freeSlots: PropTypes.arrayOf(PropTypes.shape({}).isRequired).isRequired,
  hasAnother: PropTypes.bool.isRequired,
  onEnqueue: PropTypes.func.isRequired,
};

export default Talk;
