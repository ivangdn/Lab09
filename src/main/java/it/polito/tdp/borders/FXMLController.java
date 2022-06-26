
package it.polito.tdp.borders;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.borders.model.Country;
import it.polito.tdp.borders.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML // fx:id="cmbStato"
    private ComboBox<Country> cmbStato; // Value injected by FXMLLoader
    
    @FXML // fx:id="hboxStati"
    private HBox hboxStati; // Value injected by FXMLLoader

    @FXML // fx:id="txtAnno"
    private TextField txtAnno; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaConfini(ActionEvent event) {
    	txtResult.clear();
    	cmbStato.getItems().clear();
 
    	int anno;
    	try {
    		anno = Integer.parseInt(txtAnno.getText());
    		if(anno<1816 || anno>2016) {
    			txtResult.setText("Inserire un anno nell'intervallo 1816 - 2016");
    			hboxStati.setDisable(true);
        		return;
    		}
    		
    	} catch(NumberFormatException e) {
    		txtResult.setText("Inserire un anno nell'intervallo 1816 - 2016");
    		hboxStati.setDisable(true);
    		return;
    	}
    	
    	hboxStati.setDisable(false);
    	
    	this.model.createGraph(anno);
    	List<Country> countries = this.model.getCountries();
    	cmbStato.getItems().addAll(countries);
    	
    	int componentiConnesse = this.model.getNumberOfConnectedComponents();
    	Map<Country, Integer> stats = this.model.getCountryCounts();
    	
    	txtResult.appendText("Numero di componenti connesse nel grafo: "+componentiConnesse+"\n");
    	
    	for(Country c : stats.keySet()) {
    		txtResult.appendText(String.format("%s %d\n", c, stats.get(c)));
    	}

    }
    
    @FXML
    void doRicercaStatiRaggiungibili(ActionEvent event) {
    	txtResult.clear();
    	
    	Country stato = cmbStato.getSelectionModel().getSelectedItem();
    	if(stato==null) {
    		txtResult.setText("Selezionare uno Stato");
    		return;
    	}
    	
    	
    	List<Country> statiRaggiungibili = this.model.getStatiRaggiungibili(stato);
    	for(Country c : statiRaggiungibili) {
    		txtResult.appendText(c.toString()+"\n");
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	assert cmbStato != null : "fx:id=\"cmbStato\" was not injected: check your FXML file 'Scene.fxml'.";
    	assert hboxStati != null : "fx:id=\"hboxStati\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}
