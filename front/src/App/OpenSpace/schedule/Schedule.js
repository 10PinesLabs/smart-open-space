import React, { useState } from 'react';
import { useSlots } from '#api/sockets-client';
import { useGetOpenSpace } from '#api/os-client';
import MainHeader from '#shared/MainHeader';
import { ScheduleIcon } from '#shared/icons';
import {
  RedirectToLoginFromOpenSpace,
  RedirectToRoot,
  usePushToOpenSpace,
} from '#helpers/routes';
import Spinner from '#shared/Spinner';
import { useUser } from '#helpers/useAuth';
import { ButtonSingIn } from '#shared/ButtonSingIn';
import { sortTimes, byDate } from '#helpers/time';
import { DateSlots } from './DateSlots';
import { Tab, Tabs, Select, Box } from 'grommet';
import { compareAsc, format } from 'date-fns';
import { ButtonMyTalks } from '../buttons/ButtonMyTalks';
import RowBetween from '#shared/RowBetween';

const Schedule = () => {
  const user = useUser();
  const [redirectToLogin, setRedirectToLogin] = useState(false);
  const [trackFilter, setTrackFilter] = useState('Todas');
  const [roomFilter, setRoomFilter] = useState('Todas');

  const {
    data: { id, name, slots, organizer, dates, tracks, rooms } = {},
    isPending,
    isRejected,
  } = useGetOpenSpace();
  const slotsSchedule = useSlots();
  const pushToOpenSpace = usePushToOpenSpace(id);

  if (isPending) return <Spinner />;
  if (isRejected || !dates) return <RedirectToRoot />;

  const trackNames = ['Todas', ...tracks.map((track) => track.name)];
  const roomNames = ['Todas', ...rooms.map((room) => room.name)];
  const sortedSlots = sortTimes(slots);
  const sortedDates = dates.sort(compareAsc);
  const talksOf = (slotId) =>
    slotsSchedule.filter((slotSchedule) => slotSchedule.slot.id === slotId);
  const amTheOrganizer = user && organizer.id === user.id;

  return (
    <>
      <MainHeader>
        <MainHeader.TitleLink onClick={pushToOpenSpace}>{name}</MainHeader.TitleLink>
        <MainHeader.SubTitle icon={ScheduleIcon} label="Agenda" />
        <MainHeader.Buttons>
          {user ? (
            <ButtonMyTalks amTheOrganizer={amTheOrganizer} />
          ) : (
            <ButtonSingIn onClick={() => setRedirectToLogin(true)} />
          )}
        </MainHeader.Buttons>
      </MainHeader>
      <RowBetween direction={'row'} margin={{ vertical: 'medium' }}>
        <Box>
          <MainHeader.SubTitle label="Filtrar por tracks:" />
          <Select
            options={trackNames}
            value={trackFilter}
            onChange={({ option }) => setTrackFilter(option)}
          />
        </Box>
        <Box>
          <MainHeader.SubTitle label="Filtrar por salas:" />
          <Select
            options={roomNames}
            value={roomFilter}
            onChange={({ option }) => setRoomFilter(option)}
          />
        </Box>
      </RowBetween>
      <Tabs>
        {sortedDates.map((date) => (
          <Tab title={format(date, 'yyyy-MM-dd')}>
            <DateSlots
              talksOf={talksOf}
              sortedSlots={sortedSlots.filter(byDate(date))}
              trackFilter={trackFilter}
              roomFilter={roomFilter}
            />
          </Tab>
        ))}
      </Tabs>
      {redirectToLogin && <RedirectToLoginFromOpenSpace openSpaceId={id} />}
    </>
  );
};
export default Schedule;
