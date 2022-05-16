import React from 'react';

import { Button, Form, FormField, Select, TextArea } from 'grommet';
import PropTypes from 'prop-types';

import MyProps from '#helpers/MyProps';
import useLoading from '#helpers/useLoading';
import { EmailIcon, LinkIcon, PasswordIcon, TextAreaIcon, TextIcon } from '#shared/icons';
import { TinySpinner } from '#shared/Spinner';
import Row from './Row';
import RowBetween from './RowBetween';
import { validateUrl } from '#helpers/validateUrl';

import { TextAreaWithCharacterCounter } from '#shared/TextAreaWithCharacterCounter';


const MyField = ({ icon, label, ...props }) => (
  <FormField
    label={
      <Row>
        {icon}
        {label}
      </Row>
    }
    required
    {...props}
  />
);
MyField.propTypes = { icon: PropTypes.node, label: PropTypes.string };

const MyFieldText = (props) => (
  <MyField icon={<TextIcon />} label="Nombre" name="name" {...props} />
);

const MyFieldMeeting = (props) => (
  <MyField
    icon={<LinkIcon />}
    label="Meeting"
    name="meeting"
    validate={validateUrl}
    required={false}
    {...props}
  />
);

const MyFieldTextArea = (props) => (
  <MyField
    icon={<TextAreaIcon />}
    label="Descripción"
    name="description"
    component={(props) => <TextArea {...props} />}
    required={false}
    {...props}
  />
);

TextAreaWithCharacterCounter.propTypes = { props: PropTypes.any };
const MyFieldTextAreaWithCounter = (props) => (
  <MyField
    icon={<TextAreaIcon />}
    label="Descripción"
    name="description"
    component={(props) => <TextAreaWithCharacterCounter {...props} />}
    required={false}
    {...props}
  />
);

const MyFieldEmail = (props) => (
  <MyField icon={<EmailIcon />} label="Email" name="email" type="email" {...props} />
);

const MyFieldPassword = (props) => (
  <MyField
    icon={<PasswordIcon />}
    label="Contraseña"
    name="password"
    type="password"
    {...props}
  />
);

const MyFieldSelect = (props) => (
  <MyField
    label="Elegir"
    name="select"
    component={({ onChange, ...props }) => (
      <Select
        onChange={(e) => onChange({ ...e, target: { value: e.value } })}
        {...props}
      />
    )}
    {...props}
  />
);

const Footer = ({ children }) => (
  <RowBetween margin={{ vertical: 'medium' }} justify="evenly">
    {children}
  </RowBetween>
);
Footer.propTypes = { children: MyProps.children.isRequired };

const MyForm = ({
  children,
  onSecondary,
  onSubmit,
  primaryLabel = 'Aceptar',
  secondaryLabel = 'Cancelar',
  ...props
}) => {
  const [loading, withLoading] = useLoading();
  return (
    <Form
      messages={{ invalid: 'Inválido', required: 'Obligatorio' }}
      onSubmit={withLoading(onSubmit)}
      {...props}
    >
      {children}
      <Footer>
        {onSecondary && (
          <Button disabled={loading} label={secondaryLabel} onClick={onSecondary} />
        )}
        <Button
          disabled={loading}
          icon={loading ? <TinySpinner /> : undefined}
          label={primaryLabel}
          primary
          type="submit"
        />
      </Footer>
    </Form>
  );
};
MyForm.propTypes = {
  children: MyProps.children,
  onSecondary: PropTypes.func,
  onSubmit: PropTypes.func.isRequired,
  primaryLabel: PropTypes.string,
  secondaryLabel: PropTypes.string,
};

MyForm.Text = MyFieldText;
MyForm.TextArea = MyFieldTextArea;
MyForm.TextAreaWithCharacterCounter = MyFieldTextAreaWithCounter;
MyForm.Email = MyFieldEmail;
MyForm.Field = MyField;
MyForm.Password = MyFieldPassword;
MyForm.Select = MyFieldSelect;
MyForm.Link = MyFieldMeeting;

export default MyForm;
