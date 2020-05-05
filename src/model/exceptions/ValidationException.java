package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {  //exceção para validar um formulário (mensagens de erro do formulário)

	private static final long serialVersionUID = 1L;

	private Map<String, String> errors = new HashMap<>();  //coleção de pares chave (campo) e valor (erro): cada campo (nome, cpf) é um erro diferente

	public ValidationException(String msg) {
		super(msg);
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void addError(String fieldName, String errorMessage) {  //adiciona tipos de erros
		errors.put(fieldName, errorMessage);
	}
}  