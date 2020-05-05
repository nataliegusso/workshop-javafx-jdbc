package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {  //exce��o para validar um formul�rio (mensagens de erro do formul�rio)

	private static final long serialVersionUID = 1L;

	private Map<String, String> errors = new HashMap<>();  //cole��o de pares chave (campo) e valor (erro): cada campo (nome, cpf) � um erro diferente

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