import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;

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
/**
 * Algoritmo genético del Problema del Viajante ( o TSM - Travelling Salesman Problem)
 * 
 * @author Ander
 *
 */
public class Algoritmo {

	/**
	 * ARCHIVOS POSIBLES PARA CARGAR: 
	 * 		- "resources/valoresConf.txt" 
	 * 		- "resources/valoresConf2.txt" 
	 * 		- "resources/valoresConf3.txt"
	 */
	private static final String ficheroRuta = "resources/valoresConf.txt";
	/**
	 * Muestra las poblaciones con sus individuos y evaluaciones (phenotype) de cada generación [estando en true]
	 */
	private final static boolean debugViewGenerations = false;
	static int ret;

	public static void main(String[] args) {

		// Leemos el fichero de configuración
		String[] leido = leerFicheroConf(ficheroRuta);
		// Obtenemos los valores y los asignamos a las variables
		String[] valores = leido[1].split(",");

		int populationSize = Integer.parseInt(valores[0]);
		int maxGeneraciones = Integer.parseInt(valores[1]);
		int maxPhenotypeAge = Integer.parseInt(valores[2]);
		double probPMX = Double.parseDouble(valores[3]);
		double probSwapMutator = Double.parseDouble(valores[4]);

		System.out.println("+-------------------------------------------+");
		System.out.println(String.format("|%-43s|", "         Valores de configuración"));
		System.out.println("+-------------------------------------------+");
		System.out.println("|Fichero cargado: " + ficheroRuta + " |");

		System.out.println(String.format("|%-43s|", "POPULATION SIZE: " + populationSize));
		System.out.println(String.format("|%-43s|", "MAX GENERATIONS: " + maxGeneraciones));
		System.out.println(String.format("|%-43s|", "MAX PHENOTYPE AGE: " + maxPhenotypeAge));
		System.out.println(String.format("|%-43s|", "PROB PARTIALLY MATCHED CROSSOVER: " + probPMX));
		System.out.println(String.format("|%-43s|", "PROB SWAP MUTATOR: " + probSwapMutator));
		System.out.println("+-------------------------------------------+");

		ret = 0;

		// **************************************************************************************************

		ISeq<String> alleles = ISeq.of("ciudad A", "ciudad B", "ciudad C", "ciudad D", "ciudad E", "ciudad F");

		// 1. Creo el genotipo del problema, formado por la permutación de ciudades
		Factory<Genotype<EnumGene<String>>> gtFactory = Genotype.of(PermutationChromosome.of(alleles));

		// 3.
		final Engine<EnumGene<String>, Integer> engine = Engine.builder(Algoritmo::evaluarAptitud, gtFactory)
				.optimize(Optimize.MINIMUM).populationSize(populationSize) // Tamaño de población de cada generación (=
																			// numero de individuos)
				.maximalPhenotypeAge(maxPhenotypeAge) // Número máximo de edad(generaciones)que puede durar un individuo
														// (phenotype)
				.survivorsSelector(new RouletteWheelSelector<>()) // selecciona los survivors de la descendencia con
																	// roulette wheel (metodo probabilistico)
				.offspringSelector(new TournamentSelector<>()) // selecciona la descendencia, tournament 2 individuos
				.alterers(new PartiallyMatchedCrossover<>(probPMX), new SwapMutator<>(probSwapMutator))
				.genotypeValidator(Algoritmo::isGenotypeValid).build();

		final EvolutionStatistics<Integer, ?> statistics = EvolutionStatistics.ofNumber();

		StringBuilder strDebug = new StringBuilder();

		// 4. Crea el escenario de todo
		final Phenotype<EnumGene<String>, Integer> best = engine.stream()
				// La evolución se para al realizar X generaciones
				.limit(maxGeneraciones)

				.peek(er -> strDebug.append("Generación " + er.getGeneration() + "\n"))
				// .peek(er -> strDebug.append("Genotype: " + er.getGenotypes() + "\n"))

				// Imprime la población (genotipo+phenotype) de cada generación
				.peek(er -> strDebug.append("Population: " + er.getPopulation() + "\n"))

				// Imprime el mejor phenotype de cada generación
				.peek(er -> strDebug.append("BestPhenotype: " + er.getBestPhenotype() + "\n\n"))

				// Actualiza en las estadísticas de la evaluación el resumen global de todas las
				// generaciones
				.peek(statistics)
				// Reduce la evolución al mejor phenotype
				.collect(EvolutionResult.toBestPhenotype());

		// **************************************************************************************************
		// Tratamiento de la salida
		String out = "";
		out = strDebug.toString().replace("[[", "[\n\t[");
		out = out.replace(",", "\n\t");

		if (debugViewGenerations) {
			System.out.println("\t***** DEBUG mode ON *****");
			System.out.println(out);
		} else {
			System.out.println("\t***** DEBUG mode OFF *****");
		}

		System.out.println(statistics);
		System.out.println("Mejor camino: " + best);

	}

	/**
	 * Evalúa la aptitud de cada individuo (genotipo según la nomenclatura de Jenetics),
	 * dando lugar al phenotype (= genotype + fitness value)
	 * 
	 * @param gt
	 * @return
	 */
	private static int evaluarAptitud(final Genotype<EnumGene<String>> gt) {
		// creamos nuestras ciudades y los caminos entre ellas
		Ruta ruta = new Ruta();

		String[] camino = new String[6];

		for (int i = 0; i < camino.length; i++) {

			camino[i] = gt.getChromosome(0).getGene(i).toString();
		}

		ret = ruta.calcularCoste(camino);
		return ret;
	}

	/**
	 * Comprueba si el genotipo es válido, es decir, 
	 * si la permutación de las ciudades es válida realmente.
	 * Si no es válida clasifica ese individuo como inválido
	 * 
	 * @param gt
	 * @return
	 */
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

	/**
	 * Lectura de los datos de configuración de un fichero .txt
	 * 
	 * @param ficheroRuta
	 * @return
	 */
	private static String[] leerFicheroConf(String ficheroRuta) {

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		StringBuilder confStr = new StringBuilder();

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File(ficheroRuta);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			// Lectura del fichero
			String linea;
			while ((linea = br.readLine()) != null) {
				confStr.append(linea + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		String[] ret = confStr.toString().split("\n");

		return ret;
	}

}
