-- Estimativas geradas pelo modelo de Machine Learning do grupo (origem = MODELO_ML)
create table estimativas_atraso(
    id bigint not null auto_increment,
    linha_id bigint not null,
    minutos_estimados int not null,
    probabilidade_atraso decimal(4,3) not null,
    modelo_versao varchar(50) not null,
    gerado_em datetime not null,
    origem varchar(30) not null,

    primary key(id),
    constraint fk_estimativas_linha_id foreign key (linha_id) references linhas(id),
    constraint ck_estimativas_minutos check (minutos_estimados >= 0),
    constraint ck_estimativas_probabilidade check (probabilidade_atraso >= 0 and probabilidade_atraso <= 1)
);
