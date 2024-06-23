alter table open_space
    add column if not exists is_active_voting boolean default false;