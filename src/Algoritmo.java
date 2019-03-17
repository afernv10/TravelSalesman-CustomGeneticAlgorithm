

import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.PermutationChromosome;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;

public class Algoritmo {

	//static String[] cam = new String[6];
	static int ret;

	// 2. evaluar aptitud
	private static int evaluarAptitud(final Genotype<EnumGene<String>> gt) {
		// creamos nuestras ciudades y los caminos entre ellas
		Ruta ruta = new Ruta();
		
		String[] camino = new String[6];
		
		//System.out.println(ruta.getNumeroCiudades());
		
		for (int i = 0; i < camino.length; i++) {
			
			camino[i] = gt.getChromosome(0).getGene(i).toString();
			//System.out.println(camino[i]);
		}
		
		//System.out.println(ruta.calcularCoste(camino));
		//cam = Arrays.copyOf(camino, camino.length);
		ret = ruta.calcularCoste(camino);
		return ret;
	}
	
	private static Boolean isGenotypeValid(final Genotype<EnumGene<String>> gt) {
		int apt = evaluarAptitud(gt);
		Ruta ruta = new Ruta();
		int maxValor = ruta.getMaxValor();
		
		if (apt == 0 || apt > maxValor) {
			return false;
		} else {
			return true;
		}
	}

	public static void main(String[] args) {
		
		ret = 0;
		
		ISeq<String> alleles = ISeq.of("ciudad A", "ciudad B", "ciudad C", "ciudad D", "ciudad E", "ciudad F");
		
		// 1. Creo el genotipo del problema, formado por la permutación de ciudades
		Factory<Genotype<EnumGene<String>>> gtFactory = Genotype.of(PermutationChromosome.of(alleles));
        
		
		// 3. 
		final Engine<EnumGene<String>, Integer> engine = Engine
	        	.builder(
	                        Algoritmo::evaluarAptitud,
	                        gtFactory)
	        	.optimize(Optimize.MINIMUM)
	        	.populationSize(80)
	        	.maximalPhenotypeAge(10)
	        	.survivorsSelector(new RouletteWheelSelector<>())
	        	.offspringSelector(new TournamentSelector<>())	// tournament 2 individuos
	        	.alterers(new PartiallyMatchedCrossover<>(0.6),
	        			new SwapMutator<>(0.2))
	        	.genotypeValidator(Algoritmo::isGenotypeValid)
	        	.build();
		 
		final EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();
		//EvolutionResult<EnumGene<String>, Integer> er;
		//final ISeq<Genotype<EnumGene<String>>> er2  = er.getGenotypes();
		StringBuilder strDebug = new StringBuilder();
		
		// crea el escenario de todo
		final Phenotype<EnumGene<String>, Integer> best =
	            engine.stream()
	            // La evolución se para al realizar X generaciones
	            .limit(10)
	            
	            .peek(er -> strDebug.append("G" + er.getGeneration() + "\n"))
	            //.peek(er -> System.out.println("Genotype: " + er.getGenotypes()))
	            .peek(er -> strDebug.append("Genotype: " + er.getGenotypes()  + "\n"))
	            
	            //Imprime la población (genotipo+phenotype) de cada generación
	            .peek(er -> strDebug.append("Population: " + er.getPopulation() + "\n"))
	            
	            // Imprime el mejor phenotype de cada generación
	            .peek(er -> strDebug.append("BestPhenotype: " + er.getBestPhenotype() + "\n\n"))
	            
	            // Actualiza en las estadísticas de la evaluación el resumen global de todas las generaciones
	            .peek(statistics)
	            // Reduce la evolución al mejor phenotype
	            .collect(EvolutionResult.toBestPhenotype());
		
		System.out.println(strDebug);
		System.out.println(statistics);
		System.out.println(best);
		
	}

}
