package br.com.caelum.pm73.dominio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Leilao {

	@Id @GeneratedValue
	private int id;
	private String nome;
	private Double valorInicial;
	@ManyToOne
	private Usuario dono;
	private Calendar dataAbertura;
	private boolean usado;
	private boolean encerrado;
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="leilao")
	private List<Lance> lances;
	
	public Leilao() {
		this.lances = new ArrayList<Lance>();
		this.dataAbertura = Calendar.getInstance();
	}
	
	public Leilao(String nome, Double valorInicial, Usuario dono, boolean usado) {
		this();
		this.nome = nome;
		this.valorInicial = valorInicial;
		this.dono = dono;
		this.usado = usado;
	}

	public void setDataAbertura(Calendar dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public Calendar getDataAbertura() {
		return dataAbertura;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setValorInicial(Double valorInicial) {
		this.valorInicial = valorInicial;
	}
	
	public Double getValorInicial() {
		return valorInicial;
	}
	
	public void setDono(Usuario usuario) {
		this.dono = usuario;
	}
	
	public Usuario getDono() {
		return dono;
	}

	public boolean isUsado() {
		return usado;
	}

	public void setUsado(boolean usado) {
		this.usado = usado;
	}

	public List<Lance> getLances() {
		return lances;
	}

	public int getId() {
		return id;
	}

	public void encerra() {
		this.encerrado = true;
	}

	public boolean isEncerrado() {
		return encerrado;
	}
	
	public Lance adicionaLance(Lance lance) {
		lance.setLeilao(this);
		lances.add(lance);
		return lance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataAbertura == null) ? 0 : dataAbertura.hashCode());
		result = prime * result + ((dono == null) ? 0 : dono.hashCode());
		result = prime * result + (encerrado ? 1231 : 1237);
		result = prime * result + id;
		result = prime * result + ((lances == null) ? 0 : lances.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + (usado ? 1231 : 1237);
		result = prime * result + ((valorInicial == null) ? 0 : valorInicial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Leilao other = (Leilao) obj;
		if (dataAbertura == null) {
			if (other.dataAbertura != null)
				return false;
		} else if (!dataAbertura.equals(other.dataAbertura))
			return false;
		if (dono == null) {
			if (other.dono != null)
				return false;
		} else if (!dono.equals(other.dono))
			return false;
		if (encerrado != other.encerrado)
			return false;
		if (id != other.id)
			return false;
		if (lances == null) {
			if (other.lances != null)
				return false;
		} else if (!lances.equals(other.lances))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (usado != other.usado)
			return false;
		if (valorInicial == null) {
			if (other.valorInicial != null)
				return false;
		} else if (!valorInicial.equals(other.valorInicial))
			return false;
		return true;
	}
	
	
}
