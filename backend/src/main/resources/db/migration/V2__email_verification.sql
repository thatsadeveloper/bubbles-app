create table verification_tokens (
  id bigserial primary key,
  user_id bigint not null references users(id) on delete cascade,
  token varchar(255) not null unique,
  expires_at timestamp with time zone not null,
  used boolean not null default false
);


