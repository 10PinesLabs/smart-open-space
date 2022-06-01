import React from 'react';
import { useHistory } from 'react-router-dom';

import { useGetOpenSpace, createTalk } from '#api/os-client';
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
  const { data: openSpace, isPending, isRejected } = useGetOpenSpace();

  if (!user || isRejected) return <RedirectToRoot />;
  if (openSpace && openSpace.finishedQueue) return <RedirectToRoot />;

  const onSubmit = ({ value: { name, description, meetingLink, trackId } }) =>
    createTalk(openSpace.id, {
      name,
      description,
      meetingLink,
      trackId,
    }).then(pushToMyTalks);
  return (
    <>
      <MainHeader>
        <MainHeader.Title icon={TalkIcon} label="Nueva Charla" />
        <MainHeader.SubTitle>
          {isPending ? <TinySpinner /> : openSpace.name}
        </MainHeader.SubTitle>
      </MainHeader>
      <MyForm onSecondary={history.goBack} onSubmit={onSubmit}>
        <MyForm.Text label="Título" placeholder="¿De que trata tu charla?" />
        <MyForm.TextArea placeholder="Describí tu charla con mas detalle..." />
        <MyForm.Link label="Link" placeholder="Link a la reunion" />
        {openSpace && (
          <MyForm.Select
            label="Track"
            name="trackId"
            options={openSpace.tracks}
            labelKey="name"
            valueKey="id"
          />
        )}
      </MyForm>
    </>
  );
};

export default EditTalk;
