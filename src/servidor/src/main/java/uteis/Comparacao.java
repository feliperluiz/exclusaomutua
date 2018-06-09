package servidor.src.main.java.uteis;

import java.util.Comparator;

import servidor.src.main.java.model.Processo;

public class Comparacao implements Comparator<Processo>{

	public int  compare(Processo first, Processo second) {
		if(first.getPidId() > second.getPidId())
			return 1;
		if(first.getPidId() < second.getPidId())
			return -1;
		return 0;
	}
}
