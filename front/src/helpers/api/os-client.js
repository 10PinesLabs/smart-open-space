import { useAsync } from 'react-async';
import { useParams } from 'react-router-dom';

import { console_log_debug } from '#helpers/logging';
import { get, post, put, remove } from './api-client';
import { getUser } from '../useAuth';
import { OpenSpace } from '../../App/model/openSpace';

const withUser = (fn) => fn(getUser());

const createOS = (osData) => withUser(({ id }) => post(`openSpace/${id}`, osData));
const updateOS = (openSpaceId, osData) =>
  withUser(({ id }) => put(`openSpace/${openSpaceId}/user/${id}`, osData));

const deleteOS = (osID) => withUser(({ id }) => remove(`openSpace/${osID}/user/${id}`));

const createTalkFor = (userId, openSpaceId, talkData) =>
  post(`openSpace/talk/${userId}/${openSpaceId}`, talkData);

const deleteTalkFor = (id, openSpaceId, talkID) =>
  remove(`openSpace/${openSpaceId}/talk/${talkID}/user/${id}`);

const editTalkOf = (userId, openSpaceId, talkId, talkData) =>
  put(`talk/${talkId}/user/${userId}`, talkData);

const createTalk = (openSpaceId, talkData) =>
  withUser(({ id }) => createTalkFor(id, openSpaceId, talkData));

const deleteTalk = (openSpaceId, talkID) =>
  withUser(({ id }) => deleteTalkFor(id, openSpaceId, talkID));

const editTalk = (openSpaceId, talkId, talkData) =>
  withUser(({ id }) => editTalkOf(id, openSpaceId, talkId, talkData));

const voteTalk = (talkID) =>
  withUser(({ id: userID }) => put(`talk/${talkID}/user/${userID}/vote`));

const unvoteTalk = (talkID) =>
  withUser(({ id: userID }) => put(`talk/${talkID}/user/${userID}/unvote`));

const createReview = (talkId, reviewData) =>
  withUser(({ id }) => post(`talk/${talkId}/user/${id}/review`, reviewData));

const getAllOpenSpaces = () =>
  withUser(({ id }) => get(`openSpace/user/${id}`)).then((openSpaces) =>
    openSpaces.map((openSpace) => new OpenSpace(openSpace))
  );
const useGetAllOpenSpaces = () => useAsync({ promiseFn: getAllOpenSpaces });

const getOpenSpace = ({ osId: openSpaceId }) =>
  get(`openSpace/${openSpaceId}`).then((openSpaceDTO) => {
    console_log_debug('openSpaceDTO', openSpaceDTO);
    return new OpenSpace(openSpaceDTO);
  });
const useGetOpenSpace = () => useAsync({ promiseFn: getOpenSpace, osId: useParams().id });

const getTalk = ({ talkId }) => get(`talk/${talkId}`);
const useGetTalk = () => useAsync({ promiseFn: getTalk, talkId: useParams().talkId });

const getTalks = ({ osId }) => get(`openSpace/talks/${osId}`).then((talks) => talks);
const useGetTalks = () => useAsync({ promiseFn: getTalks, osId: useParams().id });

const scheduleTalk = (talkID, userID, slotId, roomID) =>
  put(`talk/schedule/${userID}/${talkID}/${slotId}/${roomID}`);

const exchangeTalk = (talkID, slotId, roomID) =>
  put(`talk/exchange/${talkID}/${slotId}/${roomID}`);

const startCallForPapers = (openSpaceId) =>
  withUser(({ id }) => put(`openSpace/${openSpaceId}/user/${id}/callForPapers`));

const toggleVoting = (openSpaceId) =>
  withUser(({ id }) => put(`openSpace/${openSpaceId}/user/${id}/voting`));

const activateQueue = (osId) =>
  withUser(({ id }) => put(`openSpace/activateQueue/${id}/${osId}`));

const finishQueue = (osId) =>
  withUser(({ id }) => put(`openSpace/finishQueue/${id}/${osId}`));

const enqueueTalk = (talkId) =>
  withUser(({ id }) => put(`openSpace/enqueueTalk/${id}/${talkId}`));

const nextTalk = (osId) => withUser(({ id }) => put(`talk/nextTalk/${id}/${osId}`));

const getCurrentUserTalks = ({ osId: openSpaceId }) =>
  withUser(({ id }) => {
    const os = getOpenSpace({ osId: openSpaceId });
    const assignedSlots = get(`openSpace/assignedSlots/${openSpaceId}`);
    const currentUserTalks = get(`openSpace/talks/${id}/${openSpaceId}`).then(
      (talks) => talks
    );
    return Promise.all([os, assignedSlots, currentUserTalks]);
  });
const useGetCurrentUserTalks = () =>
  useAsync({ promiseFn: getCurrentUserTalks, osId: useParams().id });

export {
  activateQueue,
  createOS,
  updateOS,
  deleteOS,
  createTalk,
  createTalkFor,
  enqueueTalk,
  finishQueue,
  nextTalk,
  useGetAllOpenSpaces,
  useGetOpenSpace,
  useGetTalks,
  useGetCurrentUserTalks,
  scheduleTalk,
  exchangeTalk,
  startCallForPapers,
  voteTalk,
  useGetTalk,
  editTalk,
  unvoteTalk,
  deleteTalk,
  createReview,
  toggleVoting,
};
