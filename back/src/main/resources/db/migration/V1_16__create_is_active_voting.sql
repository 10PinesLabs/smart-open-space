alter table open_space
    add column if not exists is_active_call_for_papers boolean default false;