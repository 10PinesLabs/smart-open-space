import { Text, TextArea } from 'grommet';
import React from 'react';

export const TextAreaWithCharacterCounter = ({ ...props }) => {
  return (
    <>
      <TextArea {...props} />
      <Text alignSelf={'end'} color={'#777777'}>
        {props.value.length}/{props.maxLength}
      </Text>
    </>
  );
};
