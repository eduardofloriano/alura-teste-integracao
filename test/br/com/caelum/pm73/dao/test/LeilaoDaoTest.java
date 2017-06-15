package br.com.caelum.pm73.dao.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dao.CriadorDeSessao;
import br.com.caelum.pm73.dao.LeilaoDao;
import br.com.caelum.pm73.dao.UsuarioDao;
import br.com.caelum.pm73.dominio.Lance;
import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class LeilaoDaoTest {

	Session session;
	UsuarioDao usuarioDao;
	LeilaoDao leilaoDao;
	LeilaoBuilder builder;

	@Before
	public void init() {
		this.session = new CriadorDeSessao().getSession();
		this.usuarioDao = new UsuarioDao(session);
		this.leilaoDao = new LeilaoDao(session);
		this.builder = new LeilaoBuilder();

		session.beginTransaction();
	}

	@After
	public void end() {
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void deveContarLeiloesNaoEncerrados() {

		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		Leilao ativo = new LeilaoBuilder().comDono(mauricio).comNome("Computador").comValor(1500.0).constroi();
		Leilao encerrado = new LeilaoBuilder().comDono(mauricio).comNome("Video-Game").comValor(600.0).encerrado()
				.constroi();

		// persistimos todos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);

		// invocamos a acao que queremos testar
		// pedimos o total para o DAO
		long total = leilaoDao.total();

		assertEquals(1L, total);
	}

	@Test
	public void deveRetornarZero() {

		// criamos um usuario
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criamos os dois leiloes
		Leilao ativo = new Leilao("Geladeira", 1500.0, mauricio, false);
		Leilao encerrado = new Leilao("XBox", 700.0, mauricio, false);
		encerrado.encerra();
		ativo.encerra();

		// persistimos todos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);

		// invocamos a acao que queremos testar
		// pedimos o total para o DAO
		long total = leilaoDao.total();

		assertEquals(0L, total);

	}

	@Test
	public void deveRetornarTodosLeiloes() {

		// criamos um usuario
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criamos os dois leiloes
		Leilao ativo = new Leilao("Geladeira", 1500.0, mauricio, false);
		Leilao encerrado = new Leilao("XBox", 700.0, mauricio, true);
		encerrado.encerra();

		// persistimos todos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);

		List<Leilao> novos = leilaoDao.novos();

		assertEquals(true, novos.contains(ativo));

	}

	@Test
	public void deveRetornarLeiloesAntigos() {

		// criamos um usuario
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criamos os dois leiloes
		Leilao antigo = new Leilao("Geladeira", 1500.0, mauricio, false);
		Leilao encerrado = new Leilao("XBox", 700.0, mauricio, true);
		encerrado.encerra();

		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.add(Calendar.DAY_OF_MONTH, -10);
		antigo.setDataAbertura(dataAntiga);

		// persistimos todos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(antigo);
		leilaoDao.salvar(encerrado);

		List<Leilao> antigos = leilaoDao.antigos();

		assertEquals(true, antigos.contains(antigo));

	}

	@Test
	public void deveRetornarLeiloesAntigosSeteDias() {

		// criamos um usuario
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criamos os dois leiloes
		Leilao antigo = new Leilao("Geladeira", 1500.0, mauricio, false);
		Leilao encerrado = new Leilao("XBox", 700.0, mauricio, true);
		encerrado.encerra();

		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.add(Calendar.DAY_OF_MONTH, -7);
		antigo.setDataAbertura(dataAntiga);

		// persistimos todos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(antigo);
		leilaoDao.salvar(encerrado);

		List<Leilao> antigos = leilaoDao.antigos();

		assertEquals(true, antigos.contains(antigo));

	}

	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {

		// criando as datas
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		Calendar dataDoLeilao1 = Calendar.getInstance();
		dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);
		Calendar dataDoLeilao2 = Calendar.getInstance();
		dataDoLeilao2.add(Calendar.DAY_OF_MONTH, -20);

		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criando os leiloes, cada um com uma data
		Leilao leilao1 = new Leilao("XBox", 700.0, mauricio, false);
		leilao1.setDataAbertura(dataDoLeilao1);
		Leilao leilao2 = new Leilao("Geladeira", 1700.0, mauricio, false);
		leilao2.setDataAbertura(dataDoLeilao2);

		// persistindo os objetos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);

		// invocando o metodo para testar
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

		// garantindo que a query funcionou
		assertEquals(1, leiloes.size());
		assertEquals("XBox", leiloes.get(0).getNome());
	}

	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {

		// criando as datas
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		Calendar dataDoLeilao1 = Calendar.getInstance();
		dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);

		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criando os leiloes, cada um com uma data
		Leilao leilao1 = new Leilao("XBox", 700.0, mauricio, false);
		leilao1.setDataAbertura(dataDoLeilao1);
		leilao1.encerra();

		// persistindo os objetos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao1);

		// invocando o metodo para testar
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

		// garantindo que a query funcionou
		assertEquals(0, leiloes.size());
	}

	@Test
	public void deveTrazeLeiloesDisputados() {

		Usuario luis = new Usuario("Luis Eduardo", "eduardofloriano@gmail.com");
		Usuario juquinha = new Usuario("Juca", "juca@gmail.com");
		Usuario mario = new Usuario("Mario", "quemario@gmail.com");
		Usuario irineu = new Usuario("Irineu", "vocenaosabenemeu@gmail.com");

		Leilao leilaoMouse = new LeilaoBuilder().comDono(luis).comNome("Mouse").comValor(600.0).constroi();
		Leilao leilaoTeclado = new LeilaoBuilder().comDono(luis).comNome("Teclado").comValor(800.0).constroi();
		Leilao leilaoFone = new LeilaoBuilder().comDono(luis).comNome("Fone").comValor(300.0).constroi();
		Leilao leilaoMonitor = new LeilaoBuilder().comDono(luis).comNome("Monitos").comValor(1600.0).constroi();

		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), juquinha, 800.0, leilaoMouse));
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), irineu, 1000.0, leilaoMouse));
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), mario, 700.0, leilaoMouse));

		usuarioDao.salvar(luis);
		usuarioDao.salvar(juquinha);
		usuarioDao.salvar(irineu);
		usuarioDao.salvar(mario);
		leilaoDao.salvar(leilaoMouse);
		leilaoDao.salvar(leilaoTeclado);
		leilaoDao.salvar(leilaoFone);
		leilaoDao.salvar(leilaoMonitor);

		List<Leilao> leiloes = leilaoDao.disputadosEntre(500, 2000);

		assertEquals(0, leiloes.size());

	}
	
	
	
	@Test
	public void deveConterTodosLeiloesDoUsuario(){
		
		Usuario juquinha = new Usuario("Juca", "juca@gmail.com");
		Usuario luis = new Usuario("Luis Eduardo", "eduardofloriano@gmail.com");
		
		Leilao leilaoMouse = new LeilaoBuilder().comDono(juquinha).comNome("Mouse").comValor(600.0).constroi();
		Leilao leilaoTeclado = new LeilaoBuilder().comDono(juquinha).comNome("Teclado").comValor(800.0).constroi();
		Leilao leilaoFone = new LeilaoBuilder().comDono(luis).comNome("Fone").comValor(300.0).constroi();
		
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), juquinha, 800.0, leilaoMouse));
		leilaoTeclado.adicionaLance(new Lance(Calendar.getInstance(), juquinha, 1000.0, leilaoTeclado));
		leilaoFone.adicionaLance(new Lance(Calendar.getInstance(), luis, 1000.0, leilaoFone));
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), luis, 1000.0, leilaoFone));
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), juquinha, 2000.0, leilaoFone));
		
		
		usuarioDao.salvar(juquinha);
		usuarioDao.salvar(luis);
		leilaoDao.salvar(leilaoMouse);
		leilaoDao.salvar(leilaoTeclado);
		leilaoDao.salvar(leilaoFone);
		
		List<Leilao> result = leilaoDao.listaLeiloesDoUsuario(juquinha);
		
		assertEquals(2, result.size());
				
		
	}

	
	@Test
	public void devolveValorMedioDoUsuario(){
		
		Usuario juquinha = new Usuario("Juca", "juca@gmail.com");
		Usuario luis = new Usuario("Luis Eduardo", "eduardofloriano@gmail.com");
		
		
		Leilao leilaoMouse = new LeilaoBuilder().comDono(juquinha).comNome("Mouse").comValor(600.0).constroi();
		Leilao leilaoTeclado = new LeilaoBuilder().comDono(juquinha).comNome("Teclado").comValor(800.0).constroi();
		Leilao leilaoFone = new LeilaoBuilder().comDono(luis).comNome("Fone").comValor(300.0).constroi();
		
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), juquinha, 800.0, leilaoMouse));
		leilaoTeclado.adicionaLance(new Lance(Calendar.getInstance(), juquinha, 1000.0, leilaoTeclado));
		leilaoFone.adicionaLance(new Lance(Calendar.getInstance(), luis, 1000.0, leilaoFone));
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), luis, 1000.0, leilaoFone));
		leilaoMouse.adicionaLance(new Lance(Calendar.getInstance(), juquinha, 2000.0, leilaoFone));
		
		
		
		usuarioDao.salvar(juquinha);
		usuarioDao.salvar(luis);
		leilaoDao.salvar(leilaoMouse);
		leilaoDao.salvar(leilaoTeclado);
		leilaoDao.salvar(leilaoFone);
		
		double valorMedio = leilaoDao.getValorInicialMedioDoUsuario(juquinha);
		double valorMedioCalculado = (800.0 + 1000.0)/2;
		System.out.println(valorMedio);
		System.out.println(valorMedioCalculado);
//		assertEquals(valorMedioCalculado, valorMedio);
	}
	
	@Test
	public void deveDeletarLeilao(){
		
		Usuario juquinha = new Usuario("Juca", "juca@gmail.com");
		Leilao leilaoMouse = new LeilaoBuilder().comDono(juquinha).comNome("Mouse").comValor(600.0).constroi();
		
		usuarioDao.salvar(juquinha);
		leilaoDao.salvar(leilaoMouse);
		
		leilaoDao.deleta(leilaoMouse);
		
		
		session.flush();
		session.clear();
		
		
		assertNull(leilaoDao.porId(leilaoMouse.getId()));
		
		
	}
	
	
	
}
