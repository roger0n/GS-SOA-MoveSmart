-- Previsões de chegada informadas pela SPTrans (origem = SPTRANS_OLHO_VIVO)
create table previsoes_chegada(
    id bigint not null auto_increment,
    linha_id bigint not null,
    parada_id bigint not null,
    prefixo_veiculo varchar(20),
    horario_previsto time not null,
    acessivel tinyint,
    consultado_em datetime not null,
    origem varchar(30) not null,

    primary key(id),
    constraint fk_previsoes_linha_id foreign key (linha_id) references linhas(id),
    constraint fk_previsoes_parada_id foreign key (parada_id) references paradas(id)
);
