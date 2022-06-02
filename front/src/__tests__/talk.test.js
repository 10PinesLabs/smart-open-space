import Talk from '../App/model/talk';

function anyTalk(queue = [], slots = [], openSpace = anyOpenSpaceWithNoTalksScheduled()) {
  return new Talk(
    0,
    'a talk',
    'desc',
    undefined,
    {
      email: 'email@email.com',
      id: 1,
      name: 'speaker',
    },
    undefined,
    queue,
    slots,
    openSpace
  );
}

function slotsWithTalk() {
  return [
    {
      slot: { startTime: new Date(), endTime: new Date() },
      room: { name: 'a room', description: '', id: 0 },
      talk: {
        id: 0,
        name: 'a talk',
      },
      id: 0,
    },
  ];
}

function queueWithTalk(talk) {
  return [talk];
}

function anyOpenSpaceWithNoTalksScheduled() {
  return {
    toSchedule: [],
  };
}

function anyOpenSpaceWithTalkScheduled(talk) {
  return {
    toSchedule: [talk],
  };
}

describe('talk', () => {
  it('a talk can be not assigned to a slot', () => {
    const slots = [];
    const talk = anyTalk([], slots);
    expect(talk.isAssigned()).toBe(false);
  });

  it('a talk can be assigned to a slot', () => {
    const slots = slotsWithTalk();
    const talk = anyTalk([], slots);
    expect(talk.isAssigned()).toBe(true);
  });

  it('can be not in the queue', () => {
    const queue = [];
    const talk = anyTalk(queue);

    expect(talk.isInqueue()).toBe(false);
  });

  it('can be in queue', () => {
    const queue = queueWithTalk(anyTalk());
    const talk = anyTalk(queue);

    expect(talk.isInqueue()).toBe(true);
  });

  it('can be not to schedule', () => {
    const openSpace = anyOpenSpaceWithNoTalksScheduled();
    const talk = anyTalk([], [], openSpace);

    expect(talk.isToSchedule()).toBe(false);
  });

  it('can be to schedule', () => {
    const openSpace = anyOpenSpaceWithTalkScheduled(anyTalk());
    const talk = anyTalk([], [], openSpace);

    expect(talk.isToSchedule()).toBe(true);
  });
});
