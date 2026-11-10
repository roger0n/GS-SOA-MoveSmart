create index idx_previsoes_linha_consultado on previsoes_chegada (linha_id, consultado_em);
create index idx_relatos_linha_criado on relatos_lotacao (linha_id, criado_em);
create index idx_estimativas_linha_gerado on estimativas_atraso (linha_id, gerado_em);
create index idx_linhas_letreiro on linhas (letreiro);
