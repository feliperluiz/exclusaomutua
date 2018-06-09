package multicast;

//Classe que cria e organiza os processos
//realiza eventos (localizar, enviar, receber)
public class DCSystem {

	public int nprocessos;
	//lista de processos
	private final java.util.List<Process> processos;
	
	//Cria e configura os processos 
	public DCSystem(int nprocessos) {
		this.nprocessos = nprocessos;
		this.processos = new java.util.ArrayList<Process>();
		for (int i = 0; i < nprocessos; i++) {
			this.processos.add(new Process(this, i));
		}
	}
	
	//Entrega as mensagens do evento conforme os detalhes do pacote
	public synchronized void despachaPacote(Packet packet, int senderProcessId) {
		Process process = processos.get(packet.getProcessoId());
		process.receiveEvent(packet, senderProcessId);
	}
	
	public static void main(String[] args) {
		
		// caso nao seja fornecido os 2 parametros, exibe a mensagem abaixo
		if(args.length < 2){
			System.out.println("Atencao: Forne�a os parametros para numero de processos(int) e duracao da simulacao em segundos(int).");
			System.exit(0);
		}
		int processCount = Integer.parseInt(args[0]);
		int duration = Integer.parseInt(args[1]);

		
		long timestamp = System.currentTimeMillis();
		System.out.println("Configurando a simula��o do ambiente Multicast com [" + processCount + "] processos e dura��o de execu��o de [" + duration + "] segundos.");
		DCSystem dcSystem = new DCSystem(processCount);
		
		// inicializa cada processo indivudualmente
		for(Process process : dcSystem.processos) {
			new Thread(process).start();
		}
		
		// Aguarda a dura��o especificada para executar a simula��o e depois encerra.
		try {
			Thread.sleep(duration * 1000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		System.out.println("Finalizando simula��o de ambiente de computa��o distribu�da, com dura��o de [" + ((System.currentTimeMillis() - timestamp) / 1000) + "] segundos.");
		System.exit(0);
	}
}
