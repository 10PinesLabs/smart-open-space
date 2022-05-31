alter table talk
    add column if not exists track_id bigint default null;
alter table talk
    add foreign key (track_id) references track(id);