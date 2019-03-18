/**
 * 
 * @author Ander
 *
 */
public class Ruta {

	private String ciudadA, ciudadB, ciudadC, ciudadD, ciudadE, ciudadF;
	private int AB, AC, BC, BE, CE, CD, DF, EF;
	private int maxFitness;
	
	public Ruta() {
		this.ciudadA = "ciudad A";
		this.ciudadB = "ciudad B";
		this.ciudadC = "ciudad C";
		this.ciudadD = "ciudad D";
		this.ciudadE = "ciudad E";
		this.ciudadF = "ciudad F";
		
		this.AB = 10;
		this.AC = 30;
		this.BC = 22;
		this.BE = 55;
		this.CE = 26;
		this.CD = 24;
		this.DF = 33;
		this.EF = 68;
		
		// Para comprobar que la ruta es posible entre todas las ciudades
		this.maxFitness = getMaxValor();
	}
	
	public int calcularCoste(String[] path) {
		
		int coste = 0;
		
		for (int i = 0; i < path.length-1; i++) {
			
			if (path[i].equals(this.ciudadA) && path[i+1].equals(this.ciudadB) || path[i].equals(this.ciudadB) && path[i+1].equals(this.ciudadA)) {
				coste += this.AB;
			} else if(path[i].equals(this.ciudadA) && path[i+1].equals(this.ciudadC) || path[i].equals(this.ciudadC) && path[i+1].equals(this.ciudadA)) {
				coste += this.AC;
			} else if (path[i].equals(this.ciudadB) && path[i+1].equals(this.ciudadC) || path[i].equals(this.ciudadC) && path[i+1].equals(this.ciudadB)) {
				coste += this.BC;
			} else if (path[i].equals(this.ciudadB) && path[i+1].equals(this.ciudadE) || path[i].equals(this.ciudadE) && path[i+1].equals(this.ciudadB)) {
				coste += this.BE;
				
			} else if (path[i].equals(this.ciudadC) && path[i+1].equals(this.ciudadE) || path[i].equals(this.ciudadE) && path[i+1].equals(this.ciudadC)) {
				coste += this.CE;
				
			} else if (path[i].equals(this.ciudadC) && path[i+1].equals(this.ciudadD) || path[i].equals(this.ciudadD) && path[i+1].equals(this.ciudadC)) {
				coste += this.CD;
				
			} else if (path[i].equals(this.ciudadD) && path[i+1].equals(this.ciudadF) || path[i].equals(this.ciudadF) && path[i+1].equals(this.ciudadD)) {
				coste += this.DF;
				
			} else if (path[i].equals(this.ciudadE) && path[i+1].equals(this.ciudadF) || path[i].equals(this.ciudadF) && path[i+1].equals(this.ciudadE)) {
				coste += this.EF;
			} else {
				// para que sobrepase y podamos saber si es un camino válido o no
				coste = coste + maxFitness;
			}
		}
		
		return coste;
		
	}

	public int getMaxValor() {
		
		return this.AB + this.AC + this.BC + this.BE + this.CE + this.CD + this.DF + this.EF;
	}

}
