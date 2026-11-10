package br.com.fiap3esa.movesmart.domain.parada;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Parada")
@Table(name = "paradas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Parada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer codigoSptrans;
    private String nome;
    private Double latitude;
    private Double longitude;

    public Parada(Integer codigoSptrans, String nome, Double latitude, Double longitude) {
        this.codigoSptrans = codigoSptrans;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void atualizarInformacoes(String nome, Double latitude, Double longitude) {
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
