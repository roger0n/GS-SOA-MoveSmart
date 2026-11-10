package br.com.fiap3esa.movesmart.domain;

/**
 * Deixa explícito de onde vem cada informação exibida ao passageiro:
 * dado oficial da SPTrans, relato de passageiro ou estimativa do nosso modelo de ML.
 */
public enum OrigemDado {
    SPTRANS_OLHO_VIVO,
    RELATO_USUARIO,
    MODELO_ML
}
