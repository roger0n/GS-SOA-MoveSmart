create table linhas(
    id bigint not null auto_increment,
    codigo_sptrans int not null unique,
    letreiro varchar(20) not null,
    sentido int not null,
    terminal_principal varchar(100),
    terminal_secundario varchar(100),
    circular tinyint not null default 0,
    ativa tinyint not null default 1,

    primary key(id)
);
