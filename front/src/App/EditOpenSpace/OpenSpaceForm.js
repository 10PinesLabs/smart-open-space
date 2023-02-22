import React, { useState } from 'react';
import MainHeader from '#shared/MainHeader';
import { CalendarIcon, ClockIcon, HomeIcon, TracksIcon } from '#shared/icons';
import MyForm from '#shared/MyForm';
import Tracks from './Tracks';
import Rooms from './Rooms';
import { Box, Layer, MaskedInput, Text } from 'grommet';
import Dates from './Dates';
import TimeSelector from './TimeSelector';
import PropTypes from 'prop-types';
import Title from '#shared/Title';
import Detail from '#shared/Detail';
import Spinner from '#shared/Spinner';

const OTHER_SLOT = 'OtherSlot';

const pad = (n) => (n < 10 ? '0' : '') + n;

export const splitTime = (time) =>
  time === undefined ? [0, -1] : time.split(':').map((t) => Number(t));

const InputTime = ({ onChange, start, title, value }) => {
  const [startHour, startMinutes] = splitTime(start);
  const [currentHour] = splitTime(value);
  return (
    <Box direction="row">
      <Text alignSelf="center">{title}</Text>
      <MaskedInput
        mask={[
          {
            length: [1, 2],
            options: [...Array(24 - startHour)].map((_, k) => pad(k + startHour)),
            regexp: /^(0[0-9]|1[0-9]|2[0-3]|[0-9])$/,
            placeholder: 'hh',
          },
          { fixed: ':' },
          {
            length: 2,
            options: ['00', '15', '30', '45'].filter(
              (minutes) => currentHour !== startHour || minutes > startMinutes
            ),
            regexp: /^[0-5][0-9]|[0-5]$/,
            placeholder: 'mm',
          },
        ]}
        onChange={onChange}
        plain
        value={value}
      />
    </Box>
  );
};
InputTime.propTypes = {
  onChange: PropTypes.func.isRequired,
  start: PropTypes.string,
  title: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
};

const timeRegex = /^(0[0-9]|1[0-9]|2[0-3]|[0-9]):[0-5][0-9]$/;
const validateTime = (time) => !timeRegex.test(time) && 'Hora inválida';
const newHour = (time) => new Date().setHours(...time.split(':'));

const InputSlot = ({ onExit, onSubmit, type, start }) => {
  const [startTime, setStartTime] = useState();

  return (
    <Layer onEsc={onExit} onClickOutside={onExit}>
      <Box pad="medium">
        <Box margin={{ vertical: 'medium' }}>
          <Title level="2">Horario</Title>
          <Detail>Nuevo slot</Detail>
        </Box>
        <MyForm
          onSecondary={onExit}
          onSubmit={onSubmit}
          onChange={({ startTime: time }) => setStartTime(time)}
        >
          {start === undefined ? (
            <MyForm.Field
              component={InputTime}
              name="startTime"
              title="De"
              validate={validateTime}
            />
          ) : (
            <Box>{`De ${start}`}</Box>
          )}
          <MyForm.Field
            component={InputTime}
            name="endTime"
            title="A"
            start={start || startTime}
            validate={(time, { startTime: sTime }) =>
              validateTime(time) ||
              (newHour(time) <= newHour(start || sTime) && 'Debe ser mayor al Desde')
            }
          />
          {type === OTHER_SLOT && <MyForm.Text name="description" label="Descripción" />}
        </MyForm>
      </Box>
    </Layer>
  );
};
InputSlot.propTypes = {
  onExit: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
  start: PropTypes.string,
  type: PropTypes.string.isRequired,
};

const emptyOpenSpace = {
  name: '',
  description: '',
  dates: [],
  rooms: [],
  slots: [],
  tracks: [],
};

export const OpenSpaceForm = ({
  onSubmit,
  history,
  title,
  initialValues = emptyOpenSpace,
}) => {
  const [showInputSlot, setShowInputSlot] = useState(null);
  const [availableDates, setAvailableDates] = useState(initialValues.dates || []);
  const [deletedDate, setDeletedDate] = useState();

  function isRepeated(tracks, track) {
    return tracks.filter((eachTrack) => eachTrack.name === track.name).length > 1;
  }

  function hasTracksWithRepeatedName(tracks) {
    return tracks.some((eachTrack) => isRepeated(tracks, eachTrack));
  }

  const checkSubmit = ({ value }) => {
    if (value.dates.length > 0 && value.slots.length == 0) {
      alert('Si agregaste una fecha, tenés que agregar slots');
    } else {
      onSubmit({ value });
    }
  };

  if (initialValues === undefined) return <Spinner />;

  return (
    <>
      <MainHeader>
        <MainHeader.Title label={title} />
      </MainHeader>
      <MyForm onSecondary={history.goBack} onSubmit={checkSubmit}>
        <MyForm.Text placeholder="¿Como se va a llamar?" value={initialValues.name} />
        <MyForm.TextAreaWithCharacterCounter
          placeholder="Añade una descripción."
          maxLength={1000}
          value={initialValues.description}
        />
        <MyForm.Field
          component={Tracks}
          icon={<TracksIcon />}
          label="Tracks"
          name="tracks"
          validate={(tracks) => {
            if (hasTracksWithRepeatedName(tracks))
              return 'No puede haber dos tracks con el mismo nombre';
          }}
          value={initialValues.tracks || []}
        />
        <MyForm.Field
          component={Rooms}
          icon={<HomeIcon />}
          label="Salas"
          name="rooms"
          value={initialValues.rooms || []}
        />
        <Box direction="row">
          <MyForm.Field
            component={Dates}
            icon={<CalendarIcon />}
            label="Fecha"
            name="dates"
            onRemoveItem={(date) => setDeletedDate(date)}
            onChange={(event) => setAvailableDates(event.target.value)}
            value={initialValues.dates || []}
          />
        </Box>
        <MyForm.Field
          component={TimeSelector}
          icon={<ClockIcon />}
          label="Grilla Horaria"
          name="slots"
          onNewSlot={(type, start, onSubmitSlot) => {
            setShowInputSlot({ onSubmitSlot, start, type });
          }}
          deletedDate={deletedDate}
          dates={availableDates}
          value={initialValues.slots}
        />
      </MyForm>
      {showInputSlot !== null && (
        <InputSlot
          onExit={() => setShowInputSlot(null)}
          onSubmit={(data) => {
            showInputSlot.onSubmitSlot(data);
            setShowInputSlot(null);
          }}
          start={showInputSlot.start}
          type={showInputSlot.type}
        />
      )}
    </>
  );
};
