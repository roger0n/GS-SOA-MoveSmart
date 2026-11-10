-- Relatos voluntários dos passageiros (origem = RELATO_USUARIO)
create table relatos_lotacao(
    id bigint not null auto_increment,
    linha_id bigint not null,
    usuario_id bigint,
    nivel varchar(10) not null,
    criado_em datetime not null,
    origem varchar(30) not null,

    primary key(id),
    constraint fk_relatos_linha_id foreign key (linha_id) references linhas(id),
    constraint fk_relatos_usuario_id foreign key (usuario_id) references usuarios(id) on delete set null,
    constraint ck_relatos_nivel check (nivel in ('BAIXA', 'MEDIA', 'ALTA'))
);
