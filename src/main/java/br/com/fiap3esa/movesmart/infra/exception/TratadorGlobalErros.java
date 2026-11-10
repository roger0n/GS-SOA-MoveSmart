package br.com.fiap3esa.movesmart.infra.exception;

import br.com.fiap3esa.movesmart.domain.ValidacaoException;
import br.com.fiap3esa.movesmart.domain.estimativa.EstimativaAtrasoNotFoundException;
import br.com.fiap3esa.movesmart.domain.favorito.FavoritoNotFoundException;
import br.com.fiap3esa.movesmart.domain.linha.LinhaNotFoundException;
import br.com.fiap3esa.movesmart.domain.sptrans.ServicoExternoIndisponivelException;
import br.com.fiap3esa.movesmart.domain.usuario.UsuarioNotFoundException;
import br.com.fiap3esa.movesmart.domain.usuario.ValidacaoUsuarioException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class TratadorGlobalErros {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarEntityNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(LinhaNotFoundException.class)
    public ResponseEntity<DadosMessageNotFound> tratarLinhaNotFound(LinhaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosMessageNotFound(e.getMessage()));
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<DadosMessageNotFound> tratarUsuarioNotFound(UsuarioNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosMessageNotFound(e.getMessage()));
    }

    @ExceptionHandler(EstimativaAtrasoNotFoundException.class)
    public ResponseEntity<DadosMessageNotFound> tratarEstimativaNotFound(EstimativaAtrasoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosMessageNotFound(e.getMessage()));
    }

    @ExceptionHandler(FavoritoNotFoundException.class)
    public ResponseEntity<DadosMessageNotFound> tratarFavoritoNotFound(FavoritoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosMessageNotFound(e.getMessage()));
    }

    @ExceptionHandler(ServicoExternoIndisponivelException.class)
    public ResponseEntity<DadosMessageNotFound> tratarServicoExternoIndisponivel(ServicoExternoIndisponivelException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new DadosMessageNotFound(e.getMessage()));
    }

    @ExceptionHandler({ValidacaoException.class, ValidacaoUsuarioException.class})
    public ResponseEntity<DadosMessageNotFound> tratarValidacao(RuntimeException e) {
        return ResponseEntity.unprocessableContent().body(new DadosMessageNotFound(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DadosMessageNotFound> tratarAutenticacao() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DadosMessageNotFound("Login ou senha inválidos!"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DadosMessageNotFound> tratarAcessoNegado() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DadosMessageNotFound("Acesso negado para o seu perfil!"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosMessageNotFound> tratarCorpoIlegivel() {
        return ResponseEntity.badRequest().body(new DadosMessageNotFound("Corpo da requisição inválido ou ilegível!"));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<DadosMessageNotFound> tratarParametroInvalido() {
        return ResponseEntity.badRequest().body(new DadosMessageNotFound("Parâmetro da requisição ausente ou inválido!"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosBadRequest>> tratarBadRequest(MethodArgumentNotValidException e) {
        List<FieldError> erros = e.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosBadRequest::new).toList());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosMessageNotFound> tratarGenericException(Exception e) {
        return ResponseEntity.unprocessableContent().body(new DadosMessageNotFound(e.getMessage()));
    }

    private record DadosBadRequest(String field, String message) {
        public DadosBadRequest(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

    private record DadosMessageNotFound(String message) {
    }
}
