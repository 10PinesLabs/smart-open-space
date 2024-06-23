alter table open_space
    add column if not exists show_speaker_name boolean default true;