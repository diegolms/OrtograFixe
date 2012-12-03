package br.com.android.ortografixe.informacoes;

public class Valor {
	private String pergunta;
	private boolean resposta;
	private String regra;
	private String descricao;
	
	public Valor(String valor, boolean acerto, String regra, String descricao) {
		this.pergunta = valor;
		this.resposta = acerto;
		this.regra = regra;
		this.descricao = descricao;
	}

	public String getDescricao(){
		return this.descricao;
	}

	public String getValor() {
		return pergunta;
	}
	public void setValor(String valor) {
		this.pergunta = valor;
	}
	public boolean isAcerto() {
		return resposta;
	}
	public void setAcerto(boolean acerto) {
		this.resposta = acerto;
	}
	public String getRegra() {
		return regra;
	}
	public void setRegra(String regra) {
		this.regra = regra;
	}
	
	
	
}
