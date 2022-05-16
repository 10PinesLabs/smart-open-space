import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import PropTypes from 'prop-types';
import { Box, Text, MaskedInput, Layer } from 'grommet';

import { createOS } from '#api/os-client';
import { useUser } from '#helpers/useAuth';
import { CalendarIcon, ClockIcon, HomeIcon, OpenSpaceIcon } from '#shared/icons';
import MainHeader from '#shared/MainHeader';
import MyForm from '#shared/MyForm';
import Title from '#shared/Title';
import Detail from '#shared/Detail';
import { RedirectToRoot, usePushToRoot } from '#helpers/routes';

import MyCalendar from './MyCalendar';
import Rooms from './Rooms';
import TimeSelector from './TimeSelector';

// const TALK_SLOT = 'TalkSlot';
const OTHER_SLOT = 'OtherSlot';

const pad = (n) => (n < 10 ? '0' : '') + n;

const splitTime = (time) =>
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

const beforeToday = (date) =>
  new Date(date) < new Date(new Date().setDate(new Date().getDate() - 1));

const initialValues = {
  date: new Date().toLocaleDateString(),
  rooms: [],
  slots: [],
};

const EditOpenSpace = () => {
  const history = useHistory();
  const user = useUser();
  const pushToRoot = usePushToRoot();
  const [showInputSlot, setShowInputSlot] = useState(null);

  if (!user) return <RedirectToRoot />;

  const onSubmit = ({ value: { date, name, description, rooms, slots } }) =>
    createOS({
      date: new Date(date),
      name,
      description,
      rooms: rooms.map((r) => ({ name: r })),
      slots: slots.map(({ endTime, startTime, ...rest }) => ({
        ...rest,
        endTime: splitTime(endTime),
        startTime: splitTime(startTime),
      })),
    }).then(pushToRoot);

  return (
    <>
      <MainHeader>
        <MainHeader.Title icon={OpenSpaceIcon} label="Nuevo Open Space" />
      </MainHeader>
      <MyForm onSecondary={history.goBack} onSubmit={onSubmit} value={initialValues}>
        <MyForm.Text placeholder="¿Como se va a llamar?" />
        <MyForm.TextAreaWithCharacterCounter
          placeholder="Añade una descripcion."
          maxLength={1000}
        />

        <Box direction="row">
          <MyForm.Field
            component={MyCalendar}
            icon={<CalendarIcon />}
            label="Fecha"
            name="date"
            validate={(date) =>
              beforeToday(date) && 'Ingresa una fecha mayor o igual a hoy'
            }
          />
        </Box>
        <MyForm.Field
          component={TimeSelector}
          icon={<ClockIcon />}
          label="Horarios"
          name="slots"
          onNewSlot={(type, start, onSubmitSlot) => {
            setShowInputSlot({ onSubmitSlot, start, type });
          }}
          validate={(times) => times.length < 1 && 'Ingresa al menos un slot'}
        />
        <MyForm.Field
          component={Rooms}
          icon={<HomeIcon />}
          label="Salas"
          name="rooms"
          validate={(rooms) => rooms.length < 1 && 'Ingresa al menos una sala'}
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

export default EditOpenSpace;
