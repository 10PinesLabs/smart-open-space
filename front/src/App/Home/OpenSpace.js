import React from 'react';
import { Box, Button, Image, Layer, Grid, Text } from 'grommet';
import PropTypes from 'prop-types';

import Card from '#shared/Card';
import Detail from '#shared/Detail';
import { CalendarIcon, ClockIcon } from '#shared/icons';
import Title from '#shared/Title';
import { usePushToOpenSpace } from '#helpers/routes';
import { DeleteIcon } from '#shared/icons';
import ButtonLoading from '#shared/ButtonLoading';

const pad = (n) => (n < 10 ? '0' : '') + n;
const toTime = (time) => (time.includes(':') ? time : time.map(pad).join(':'));
const ButtonAction = (props) => (
  <ButtonLoading alignSelf="center" margin={{ top: 'small' }} {...props} />
);

const OpenSpace = ({ deleteOS, startDate, endTime, id, name, startTime, urlImage }) => {
  const [showDeleteModal, setShowDeleteModal] = React.useState();
  const PushToOpenSpace = usePushToOpenSpace(id);

  return (
    <Button fill plain>
      {({ hover }) => (
        <Card
          borderColor={hover ? 'accent-1' : 'brand'}
          elevation={hover ? 'xlarge' : 'small'}
          fill
          justify="start"
          gap="small"
        >
          <Grid gap={'xsmall'}>
            <Button fill onClick={PushToOpenSpace} plain>
              <Box height="xsmall" round={{ corner: 'top' }}>
                <Image
                  src={urlImage}
                  fit="cover"
                  style={{
                    borderTopLeftRadius: 20,
                    borderTopRightRadius: 20,
                  }}
                />
              </Box>
              <Box pad="small" justify="start" gap="small">
                <Title level="3">{name}</Title>
                <Detail
                  icon={CalendarIcon}
                  text={
                    startDate
                      ? new Date(startDate).toLocaleDateString('es')
                      : 'Sin fecha aún'
                  }
                />
                <Detail
                  icon={ClockIcon}
                  text={
                    startTime && endTime
                      ? `${toTime(startTime)} a ${toTime(endTime)} hs`
                      : ''
                  }
                />
              </Box>
            </Button>
            <ButtonAction
              icon={<DeleteIcon />}
              label="Eliminar"
              onClick={() => setShowDeleteModal(true)}
            />
            {showDeleteModal && (
              <Layer
                onEsc={() => setShowDeleteModal(false)}
                onClickOutside={() => setShowDeleteModal(false)}
              >
                <Box pad="medium" gap="medium">
                  <Text>¿Estás seguro que querés eliminar esta conferencia?</Text>
                  <Box justify="around" direction="row" pad="small">
                    <Button
                      label="Si"
                      onClick={() => {
                        deleteOS();
                        setShowDeleteModal(false);
                      }}
                    />
                    <Button label="No" onClick={() => setShowDeleteModal(false)} />
                  </Box>
                </Box>
              </Layer>
            )}
          </Grid>
        </Card>
      )}
    </Button>
  );
};
OpenSpace.propTypes = {
  startDate: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.arrayOf(PropTypes.number),
    PropTypes.instanceOf(Date),
  ]).isRequired,
  endTime: PropTypes.oneOfType([PropTypes.string, PropTypes.arrayOf(PropTypes.number)])
    .isRequired,
  startTime: PropTypes.oneOfType([PropTypes.string, PropTypes.arrayOf(PropTypes.number)])
    .isRequired,
  id: PropTypes.number.isRequired,
  name: PropTypes.string.isRequired,
  urlImage: PropTypes.string.isRequired,
};

export default OpenSpace;
