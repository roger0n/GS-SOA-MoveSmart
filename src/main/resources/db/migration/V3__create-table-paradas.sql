create table paradas(
    id bigint not null auto_increment,
    codigo_sptrans int not null unique,
    nome varchar(150) not null,
    latitude double,
    longitude double,

    primary key(id)
);
