import React from 'react';
import { useHistory } from 'react-router-dom';

import { useGetOS, createTalk } from '#api/os-client';
import { useUser } from '#helpers/useAuth';
import { TalkIcon } from '#shared/icons';
import MainHeader from '#shared/MainHeader';
import MyForm from '#shared/MyForm';
import { TinySpinner } from '#shared/Spinner';
import { RedirectToRoot, usePushToMyTalks } from '#helpers/routes';

const EditTalk = () => {
  const history = useHistory();
  const user = useUser();
  const pushToMyTalks = usePushToMyTalks();
  const { data: os, isPending, isRejected } = useGetOS();

  if (!user || isRejected) return <RedirectToRoot />;
  if (os && os.finishedQueue) return <RedirectToRoot />;

  const onSubmit = ({ value: { name, description, meetingLink } }) =>
    createTalk(os.id, { name, description, meetingLink }).then(pushToMyTalks);

  return (
    <>
      <MainHeader>
        <MainHeader.Title icon={TalkIcon} label="Nueva Charla" />
        <MainHeader.SubTitle>{isPending ? <TinySpinner /> : os.name}</MainHeader.SubTitle>
      </MainHeader>
      <MyForm onSecondary={history.goBack} onSubmit={onSubmit}>
        <MyForm.Text label="Título" placeholder="¿De que trata tu charla?" />
        <MyForm.TextArea placeholder="Describí tu charla con mas detalle..." />
        <MyForm.Link label="Link" placeholder="Link a la reunion" />
      </MyForm>
    </>
  );
};

export default EditTalk;
