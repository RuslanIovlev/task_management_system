CREATE TABLE users (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    email varchar(64) unique not null,
    password varchar(64) not null,
    role varchar(64) not null
);

CREATE TABLE tasks (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64) unique not null,
    description varchar(1024) not null,
    status varchar(50) not null ,
    priority varchar(50) not null ,
    author_id bigint not null ,
    assignee_id bigint,
    foreign key (author_id) references users(id) on delete cascade ,
    foreign key (assignee_id) references users(id) on delete set null
);

CREATE TABLE comments (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    text varchar(1024) not null,
    task_id bigint not null,
    user_id bigint not null ,
    foreign key (task_id) references tasks(id) on delete cascade,
    foreign key (user_id) references users(id) on delete cascade
);
