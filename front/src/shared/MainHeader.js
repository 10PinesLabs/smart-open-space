import React from 'react';

import PropTypes from 'prop-types';
import { Box, Text, Button, Paragraph } from 'grommet';

import MyProps from '#helpers/MyProps';
import useSize from '#helpers/useSize';
import ButtonNew from './ButtonNew';
import ButtonLoading from './ButtonLoading';
import Row from './Row';
import RowBetween from './RowBetween';
import Title from './Title';

const useTextAlign = () => (useSize() === 'small' ? 'center' : 'start');

const MyTitle = ({ children, icon: Icon, label, ...props }) => (
  <Title level="2" alignSelf={useTextAlign()} {...props}>
    <Row>
      {Icon && <Icon />}
      {label}
      {children}
    </Row>
  </Title>
);
MyTitle.propTypes = {
  children: MyProps.children,
  icon: PropTypes.func,
  label: PropTypes.string,
};

const MyTitleLink = (props) => (
  <MyTitle style={{ textDecoration: 'underline' }}>
    <Button hoverIndicator plain {...props} />
  </MyTitle>
);

const MySubTitle = ({ children, icon: Icon, label, ...props }) => (
  <Text color="dark-5" size="large" textAlign={useTextAlign()} {...props}>
    <Row>
      {Icon && <Icon color="dark-5" />}
      {label}
      {children}
    </Row>
  </Text>
);
MySubTitle.propTypes = {
  children: MyProps.children,
  icon: PropTypes.func,
  label: PropTypes.string,
};

const Description = ({ children, description, ...props }) => (
  <Paragraph fill={true} {...props}>
    {description}
    {children}
  </Paragraph>
);
Description.propTypes = {
  children: MyProps.children,
  description: PropTypes.string,
};

const MyButton = (props) => <Button primary {...props} />;

const getByType = (childs, type) => childs.find((c) => c.type === type);
const getAllByTypes = (childs, ...types) =>
  childs.filter((c) => types.find((t) => t === c.type));

const Buttons = ({ children }) => <Box gap="medium">{children}</Box>;
Buttons.propTypes = { children: MyProps.children };

const MainHeader = ({ children, ...props }) => {
  const isSmall = useSize() === 'small';
  const theChildren = React.Children.toArray(children);
  const titles = getAllByTypes(theChildren, MyTitle, MyTitleLink, MySubTitle);
  const description = getByType(theChildren, Description);
  const buttons = getByType(theChildren, Buttons);
  return (
    <>
      <RowBetween
        direction={isSmall ? 'column' : 'row'}
        margin={{ vertical: isSmall ? 'large' : 'medium' }}
      >
        <Box
          align={isSmall ? 'center' : undefined}
          margin={{ bottom: isSmall ? 'large' : undefined }}
          {...props}
        >
          {titles}
        </Box>
        {buttons}
      </RowBetween>
      {description}
    </>
  );
};
MainHeader.propTypes = { children: MyProps.children.isRequired };

MainHeader.Title = MyTitle;
MainHeader.TitleLink = MyTitleLink;
MainHeader.SubTitle = MySubTitle;
MainHeader.Description = Description;
MainHeader.Button = MyButton;
MainHeader.ButtonNew = ButtonNew;
MainHeader.ButtonLoading = ButtonLoading;
MainHeader.Buttons = Buttons;

export default MainHeader;
