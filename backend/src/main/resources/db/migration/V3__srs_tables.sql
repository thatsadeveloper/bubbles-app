create table user_vocab_progress (
  id bigserial primary key,
  user_id bigint not null references users(id) on delete cascade,
  vocab_id bigint not null references vocabulary(id) on delete cascade,
  easiness numeric(4,2) not null default 2.5,
  interval_days int not null default 0,
  repetitions int not null default 0,
  last_review_at timestamp with time zone,
  next_review_at timestamp with time zone,
  unique (user_id, vocab_id)
);

create table user_sentence_progress (
  id bigserial primary key,
  user_id bigint not null references users(id) on delete cascade,
  sentence_id bigint not null references sentences(id) on delete cascade,
  easiness numeric(4,2) not null default 2.5,
  interval_days int not null default 0,
  repetitions int not null default 0,
  last_review_at timestamp with time zone,
  next_review_at timestamp with time zone,
  unique (user_id, sentence_id)
);


