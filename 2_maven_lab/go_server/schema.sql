-- A table that stores all the lists in the system.
drop table if exists list;
create table list(
    id  integer primary key autoincrement,
    name text not null
);

-- A table that stores all the tasks in the system.
drop table if exists task;
create table task(
    id integer primary key autoincrement,
    name text not null,
    done boolean default false,
    list int references list(id)
);
