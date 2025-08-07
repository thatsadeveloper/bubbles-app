create table users (
  id bigserial primary key,
  email varchar(255) not null unique,
  password_hash varchar(255) not null,
  level varchar(16),
  native_language varchar(8),
  target_language varchar(8),
  created_at timestamp with time zone default now()
);

create table bubbles (
  id bigserial primary key,
  user_id bigint references users(id) on delete cascade,
  topic varchar(255) not null,
  target_language varchar(8) not null,
  level varchar(16) not null,
  status varchar(16) not null default 'PENDING',
  started_at timestamp with time zone,
  completed_at timestamp with time zone,
  created_at timestamp with time zone default now(),
  unique (user_id, topic, target_language)
);

create table sentences (
  id bigserial primary key,
  bubble_id bigint references bubbles(id) on delete cascade,
  text text not null,
  translation text,
  difficulty int,
  order_index int,
  audio_url text
);

create table vocabulary (
  id bigserial primary key,
  bubble_id bigint references bubbles(id) on delete cascade,
  lemma varchar(255) not null,
  translation varchar(255),
  pos varchar(64),
  gender varchar(32),
  example_sentence_id bigint references sentences(id) on delete set null,
  frequency int,
  audio_url text
);

create table conversation_turns (
  id bigserial primary key,
  bubble_id bigint references bubbles(id) on delete cascade,
  speaker varchar(1) not null,
  text text not null,
  translation text,
  order_index int,
  audio_url text
);

create table exercises (
  id bigserial primary key,
  bubble_id bigint references bubbles(id) on delete cascade,
  type varchar(32) not null,
  prompt jsonb not null,
  solution jsonb not null,
  distractors_json jsonb,
  metadata_json jsonb,
  created_at timestamp with time zone default now()
);

create table exercise_attempts (
  id bigserial primary key,
  exercise_id bigint references exercises(id) on delete cascade,
  user_id bigint references users(id) on delete cascade,
  response_json jsonb not null,
  is_correct boolean,
  score numeric(5,2),
  time_spent_ms int,
  created_at timestamp with time zone default now()
);

create index if not exists idx_bubbles_status on bubbles(status);
create index if not exists idx_sentences_bubble on sentences(bubble_id);
create index if not exists idx_vocab_bubble on vocabulary(bubble_id);
create index if not exists idx_conv_bubble on conversation_turns(bubble_id);


