create sequence if not exists sqn_notification
    start with 1
    increment by 1;

create sequence if not exists sqn_email_outbox
    start with 1
    increment by 1;

create table if not exists notification
(
    id              bigint primary key default nextval('sqn_notification'),
    event_id        uuid         not null unique,
    order_id        bigint       not null,
    recipient_email varchar(255) not null,
    subject         varchar(255) not null,
    body            text         not null,
    status          varchar(50)  not null,
    error_message   text,
    created_at      timestamp    not null default now(),
    updated_at      timestamp    not null default now(),
    sent_at         timestamp
    );

create table if not exists email_outbox
(
    id              bigint primary key default nextval('sqn_email_outbox'),
    notification_id bigint       not null,
    status          varchar(50)  not null,
    attempt_count   integer      not null default 0,
    next_retry_at   timestamp,
    last_error      text,
    created_at      timestamp    not null default now(),
    updated_at      timestamp    not null default now(),
    processed_at    timestamp,

    constraint fk_email_outbox_notification
    foreign key (notification_id)
    references notification (id)
    on delete cascade
    );

create index if not exists idx_notification_order_id
    on notification(order_id);

create index if not exists idx_notification_status
    on notification(status);

create index if not exists idx_email_outbox_status
    on email_outbox(status);

create index if not exists idx_email_outbox_next_retry_at
    on email_outbox(next_retry_at);

create index if not exists idx_email_outbox_notification_id
    on email_outbox(notification_id);