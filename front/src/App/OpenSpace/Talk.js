import React from 'react';

import { Box, Button } from 'grommet';
import PropTypes from 'prop-types';

import Card from '#shared/Card';
import Detail from '#shared/Detail';
import { HomeIcon, UserIcon } from '#shared/icons';
import Title from '#shared/Title';
import { usePushToTalk } from '#helpers/routes';
import { useParams } from 'react-router-dom';

const ButtonMoreInfo = ({ onClick }) => (
  <Button
    alignSelf="center"
    color="accent-3"
    label="Mas Info"
    onClick={onClick}
    primary
  />
);
ButtonMoreInfo.propTypes = { onClick: PropTypes.func.isRequired };

const ButtonGoToLink = ({ onClick }) => (
  <Button
    alignSelf="center"
    color="accent-4"
    label="Ir a reunion"
    onClick={onClick}
    primary
  />
);

const Talk = ({
  talk: { id, name, speaker, meetingLink, track },
  room,
  children,
  showSpeakerName,
}) => {
  const pushToTalk = usePushToTalk(useParams().id, id);

  const color = track ? track.color : 'accent-3';
  const talkLink = meetingLink || room?.link;
  return (
    <>
      <Card borderColor={color} margin="xsmall" gap="small">
        {children}
        <Title>{name}</Title>
        <Box gap="medium">
          {showSpeakerName && <Detail icon={UserIcon} text={speaker.name} />}
          {room && <Detail icon={HomeIcon} text={room.name} />}
          <ButtonMoreInfo onClick={() => pushToTalk()} />
          {talkLink && <ButtonGoToLink onClick={() => window.open(talkLink, '_blank')} />}
        </Box>
      </Card>
    </>
  );
};
Talk.propTypes = {
  room: PropTypes.shape(),
  talk: PropTypes.shape({
    name: PropTypes.string.isRequired,
    description: PropTypes.string,
    speaker: PropTypes.shape({ name: PropTypes.string.isRequired }).isRequired,
  }).isRequired,
};

export default Talk;
