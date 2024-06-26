import RowBetween from '#shared/RowBetween';
import { Button } from 'grommet';
import { unvoteTalk, voteTalk } from '#api/os-client';
import React from 'react';
import PropTypes from 'prop-types';
import { useUser } from '#helpers/useAuth';
import { PlusHeartIcon } from '#shared/PlusHeartIcon';
import Detail from '#shared/Detail';
import { CrossHeartIcon } from '#shared/CrossHeartIcon';
import pluralize from 'pluralize';

export const Vote = ({ talk: { id, votingUsers, votes }, reloadTalks, activeVoting }) => {
  const currentUser = useUser();
  const isCurrentUser = (user) => currentUser && user.id === currentUser.id;
  const alreadyVotedByTheCurrentUser = votingUsers.some((user) => isCurrentUser(user));
  const canVote = currentUser && !alreadyVotedByTheCurrentUser;

  return (
    <RowBetween alignSelf="end">
      {activeVoting && canVote && (
        <Button
          plain={true}
          icon={<PlusHeartIcon />}
          onClick={() => voteTalk(id).then(() => reloadTalks())}
        />
      )}
      {activeVoting && alreadyVotedByTheCurrentUser && (
        <Button
          plain={true}
          icon={<CrossHeartIcon color="status-error" />}
          onClick={() => unvoteTalk(id).then(() => reloadTalks())}
        />
      )}
      <Detail>{pluralize('voto', votes, true)}</Detail>
    </RowBetween>
  );
};

Vote.propTypes = {
  talk: PropTypes.object.isRequired,
  reloadTalks: PropTypes.func.isRequired,
};
