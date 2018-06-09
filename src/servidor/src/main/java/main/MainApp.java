package servidor.src.main.java.main;


import servidor.src.main.java.manager.Gerenciador;

public class MainApp {

	public static void main(String[] args) {
		Gerenciador gerenciador = new Gerenciador();
		gerenciador.criar();
		gerenciador.inicializar();
		gerenciador.encerrar();
	}
}
