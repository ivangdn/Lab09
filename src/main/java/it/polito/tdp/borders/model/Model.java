package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	private Graph<Country, DefaultEdge> grafo;
	private List<Country> countries;
	private Map<Integer, Country> countryIdMap;
	private BordersDAO dao;

	public Model() {
		this.dao = new BordersDAO();
	} 
	
	public List<Country> getCountries() {
		if(this.grafo==null) {
			throw new RuntimeException("Grafo non esistente o non creato correttamente");
		}
		
		this.countries = new ArrayList<Country>(this.grafo.vertexSet());
		Collections.sort(this.countries);
		return this.countries;
	}
	
	public void createGraph(int anno) {
		this.grafo = new SimpleGraph<Country, DefaultEdge>(DefaultEdge.class);
		this.countries = dao.loadAllCountries();
		this.countryIdMap = new HashMap<Integer, Country>();
		for(Country c : countries) {
			countryIdMap.put(c.getcCode(), c);
		}
		
		List<Border> confini = dao.getCountryPairs(countryIdMap, anno);
		
		if(confini.isEmpty()) {
			throw new RuntimeException("Nessun confine trovato per l'anno selezionato");
		}
		
		for(Border b : confini) {
			this.grafo.addVertex(b.getC1());
			this.grafo.addVertex(b.getC2());
			this.grafo.addEdge(b.getC1(), b.getC2());
		}
		
//		System.out.println("Nazioni = "+this.grafo.vertexSet().size());
//		System.out.println("Confini = "+this.grafo.edgeSet().size());
		
	}
	
	public Map<Country, Integer> getCountryCounts() {
		if(this.grafo==null || this.grafo.vertexSet().isEmpty()) {
			throw new RuntimeException("Grafo non esistente o non creato correttamente");
		}
		
		Map<Country, Integer> stats = new HashMap<Country, Integer>();
		for(Country c : this.grafo.vertexSet()) {
			stats.put(c, this.grafo.degreeOf(c));
		}
		return stats;
	}
	
	public int getNumberOfConnectedComponents() {
		if(this.grafo==null) {
			throw new RuntimeException("Grafo non esistente o non creato correttamente");
		}
		
		ConnectivityInspector<Country, DefaultEdge> ci = new ConnectivityInspector<Country, DefaultEdge>(this.grafo);
		return ci.connectedSets().size();
	}
	
	public List<Country> getStatiRaggiungibili(Country country) {
		if(this.grafo==null) {
			throw new RuntimeException("Grafo non esistente o non creato correttamente");
		}
		
		if(!this.grafo.vertexSet().contains(country)) {
			throw new RuntimeException("Lo Stato selezionato non è presente nel grafo");
		}
		
		List<Country> statiRaggiungibili = this.getStatiRaggiungibiliJGraphT(country);
//		List<Country> statiRaggiungibili = this.getStatiRaggiungibiliRicorsiva(country);
//		List<Country> statiRaggiungibili = this.getStatiRaggiungibiliIterativa(country);
		Collections.sort(statiRaggiungibili);
		
		return statiRaggiungibili;
	}

	// METODO 1: metodi della libreria JGraphT
	private List<Country> getStatiRaggiungibiliJGraphT(Country country) {
		List<Country> statiRaggiungibili = new ArrayList<Country>();
		
		// METODO 1.1: BreadthFirstIterator
//		GraphIterator<Country, DefaultEdge> bfi = new BreadthFirstIterator<Country, DefaultEdge>(this.grafo, country);
//		while(bfi.hasNext()) {
//			statiRaggiungibili.add(bfi.next());
//		}
		
		// METODO 1.2: DepthFirstIterator
		GraphIterator<Country, DefaultEdge> dfi = new DepthFirstIterator<>(this.grafo, country);
		while(dfi.hasNext()) {
			statiRaggiungibili.add(dfi.next());
		}
		
		return statiRaggiungibili;
	}
	
	// METODO 2: algoritmo ricorsivo (per la visita in profondità)
	private List<Country> getStatiRaggiungibiliRicorsiva(Country country) {
		List<Country> statiRaggiungibili = new ArrayList<Country>();
		ricorsione(statiRaggiungibili, country);
		return statiRaggiungibili;
	}
	
	private void ricorsione(List<Country> statiRaggiungibili, Country country) {
		statiRaggiungibili.add(country);
		
		for(Country c : Graphs.neighborListOf(this.grafo, country)) {
			if(!statiRaggiungibili.contains(c)) {
				ricorsione(statiRaggiungibili, c);
			}
			
		}
	}
	
	// METODO 3: algoritmo iterativo
	private List<Country> getStatiRaggiungibiliIterativa(Country country) {
		List<Country> visitati = new ArrayList<Country>();
		List<Country> daVisitare = new ArrayList<Country>();
		
		daVisitare.add(country);
		
		while(!daVisitare.isEmpty()) {
			Country visitato = daVisitare.remove(0);
			for(Country c : Graphs.neighborListOf(this.grafo, visitato)) {
				if(!daVisitare.contains(c) && !visitati.contains(c)) {
					daVisitare.add(c);
				}
			}
			visitati.add(visitato);
		}
		return visitati;
	}

}
