package br.com.caelum.pm73.dao.test;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dao.CriadorDeSessao;
import br.com.caelum.pm73.dao.UsuarioDao;
import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	Session session;
	UsuarioDao usuarioDao;
	
	@Before
	public void init(){
		this.session = new CriadorDeSessao().getSession();
		this.usuarioDao = new UsuarioDao(session);
	}
	
	@After
	public void end(){
		session.close();
	}

	@Test
	public void deveEncontrarPeloNomeEEmail() {

		// criando um usuario e salvando antes
		// de invocar o porNomeEEmail
		Usuario novoUsuario = new Usuario("João da Silva", "joao@dasilva.com.br");
		usuarioDao.salvar(novoUsuario);

		// agora buscamos no banco
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("João da Silva", "joao@dasilva.com.br");

		assertEquals("João da Silva", usuarioDoBanco.getNome());
		assertEquals("joao@dasilva.com.br", usuarioDoBanco.getEmail());

		
	}
	
	
	@Test
	public void deveEncontrarPorNomeEEmail(){
		// agora buscamos no banco
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("Zeca da Silva", "zeca@dasilva.com.br");

		assertNull(usuarioDoBanco);
	}

	
	@Test
	public void deveDeletarUsuario(){
		
		Usuario juquinha = new Usuario("Juca", "juca@gmail.com");
		usuarioDao.salvar(juquinha);
		usuarioDao.deletar(juquinha);
		
		session.flush();
		session.clear();
		
		Usuario deletado = usuarioDao.porNomeEEmail("Juca", "juca@gmail.com");
		
		assertNull(deletado);
		
	}
	
	
	@Test
	public void deveAlterarUsuario(){

		Usuario juquinha = new Usuario("Juca", "juca@gmail.com");
		usuarioDao.salvar(juquinha);
		
		juquinha.setNome("Jucao");
		usuarioDao.salvar(juquinha);
		
		session.flush();
		
		Usuario alterado = usuarioDao.porNomeEEmail("Jucao", "juca@gmail.com");
		Usuario antigo = usuarioDao.porNomeEEmail("Juca", "juca@gmail.com");
		
		assertEquals("Jucao", alterado.getNome());
		assertNull(antigo);
		
	}
	
}
