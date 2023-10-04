import React from 'react';
import { useUser } from '#helpers/useAuth';
import { RedirectToRoot, usePushToEditTalk, usePushToOpenSpace } from '#helpers/routes';
import { useGetTalk, deleteTalk, createReview } from '#api/os-client';
import MainHeader from '#shared/MainHeader';
import Spinner from '#shared/Spinner';
import { useParams } from 'react-router-dom';
import { EditIcon, DeleteIcon, StarIcon } from '#shared/icons';
import { Button, Anchor, Text, Box, Layer } from 'grommet';
import Card from '#shared/Card';
import { ReviewForm } from './ReviewForm';
import Row from '#shared/Row';
import Title from '#shared/Title';
import { Return } from 'grommet-icons/icons';

const Talk = () => {
  const user = useUser();
  const {
    data: { id, name, description, documents, reviews, speaker } = {},
    isPending,
    isRejected,
  } = useGetTalk();
  const openSpaceId = useParams().id;
  const amTheSpeaker = user && speaker && speaker.id === user.id;
  const pushToEditTalk = usePushToEditTalk(id);
  const [showDeleteModal, setShowDeleteModal] = React.useState();
  const [viewReviews, setViewReviews] = React.useState([]);
  const pushToOpenSpace = usePushToOpenSpace(openSpaceId);

  React.useEffect(() => {
    setViewReviews(reviews);
  }, [reviews]);

  if (isPending) return <Spinner />;
  if (isRejected) return <RedirectToRoot />;

  return (
    <>
      <MainHeader>
        <MainHeader.Title label={name} />
        <MainHeader.Description description={description} />
        <MainHeader.Buttons>
          {amTheSpeaker && (
            <Button
              icon={<EditIcon />}
              color="accent-4"
              label="Editar"
              onClick={pushToEditTalk}
            />
          )}
          <Button
            icon={<DeleteIcon />}
            label="Eliminar"
            onClick={() => setShowDeleteModal(true)}
          />
          <Button
            color="accent-1"
            icon={<Return />}
            label="Open Space"
            onClick={pushToOpenSpace}
          />
        </MainHeader.Buttons>
      </MainHeader>
      <MainHeader.Title label={'Documentos'} level="3" margin={{ top: 'medium' }} />
      <ul>
        {documents.map((document) => (
          <li>
            <Anchor
              color="dark-1"
              href={document.link}
              label={document.name}
              target="_blank"
            />
          </li>
        ))}
      </ul>
      <MainHeader.Title label={'Feedback'} level="3" margin={{ top: 'medium' }} />
      <ReviewForm
        onSubmit={(event) =>
          createReview(id, {
            comment: event.value.comment,
            grade: event.value.grade,
          }).then((talk) => setViewReviews(talk.reviews))
        }
      />
      {viewReviews &&
        viewReviews.map((review) => (
          <Card background="light-1">
            <Box>
              <Title justify="start" textAlign="left" level="5">
                {review.reviewer.name}
              </Title>
              <Row margin="none" justify="start">
                {review.comment}
              </Row>
              <Row margin="none" justify="end">
                <StarIcon />
                {review.grade}
              </Row>
            </Box>
          </Card>
        ))}
      {showDeleteModal && (
        <Layer
          onEsc={() => setShowDeleteModal(false)}
          onClickOutside={() => setShowDeleteModal(false)}
        >
          <Box pad="medium" gap="medium">
            <Text>¿Estás seguro que querés eliminar esta charla?</Text>
            <Box justify="around" direction="row" pad="small">
              <Button
                label="Si"
                onClick={() => {
                  deleteTalk(openSpaceId, id).then(pushToOpenSpace);
                  setShowDeleteModal(false);
                }}
              />
              <Button label="No" onClick={() => setShowDeleteModal(false)} />
            </Box>
          </Box>
        </Layer>
      )}
    </>
  );
};

export default Talk;
