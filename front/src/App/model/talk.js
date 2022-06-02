export default class Talk {
  constructor(
    id,
    name,
    description,
    meetingLink,
    speaker,
    track,
    queue,
    slots,
    openSpace
  ) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.meetingLink = meetingLink;
    this.speaker = speaker;
    this.track = track;
    this.queue = queue;
    this.slots = slots;
    this.openSpace = openSpace;
  }

  canBeQueued() {
    return !this.isAssigned() && !this.isToSchedule();
  }

  isAssigned() {
    return this.isIn(this.slots.map((slot) => slot.talk));
  }

  isInqueue() {
    return this.isIn(this.queue);
  }

  isToSchedule() {
    return this.isIn(this.openSpace.toSchedule);
  }

  isIn(talks) {
    return talks.some((talk) => talk.id === this.id);
  }
}
