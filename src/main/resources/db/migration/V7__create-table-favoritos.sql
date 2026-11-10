create table favoritos(
    id bigint not null auto_increment,
    usuario_id bigint not null,
    linha_id bigint not null,

    primary key(id),
    constraint uk_favoritos_usuario_linha unique (usuario_id, linha_id),
    constraint fk_favoritos_usuario_id foreign key (usuario_id) references usuarios(id) on delete cascade,
    constraint fk_favoritos_linha_id foreign key (linha_id) references linhas(id)
);
