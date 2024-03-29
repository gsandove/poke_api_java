package main.java.View;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import main.java.Utils.Http;
import main.java.Utils.Util;
import main.java.domain.Pokemons;
import org.json.JSONArray;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ListController {

    private JSONArray pokemons;
    private List<Pokemons> pokemonsList;
    private Pokemons pokemon;
    private int page;
    private AnchorPane _anchor;

    public ListController() {
        this.page = 1;
    }

    private Label nameLabel;
    private ImageView pokeImage;
    private static final int width = 120;
    private static final int height = 120;
    @FXML private GridPane gridPane;
    @FXML private Button previousButton,nextButton,searchButton;
    @FXML private TextField nameTextField;


    private void findPokemonByName(String name){
        pokemon = null;
        pokemon = pokemonsList.stream().filter(poke->poke.getName().trim().equals(name.trim())).findFirst().orElse(null);
        setDescriptionPokemon();
    }

    private void setDescriptionPokemon(){
        pokemon.setDescription(Util.getDescriptionPokemon(pokemon.getNumber().trim()));
    }

    private String getPokemonName(Event event){
        _anchor = (AnchorPane) event.getSource();
        return ((Label) _anchor.getChildren().get(1)).getText();
    }

    private void showPokemon(Event event){
        findPokemonByName(getPokemonName(event));
        showViewPokemon();
    }

    private void showViewPokemon(){
        Util.showView("View/viewPokemon.fxml", new ViewPokemonManager(this.pokemon,this));
    }

    private void insertPokemonToGridPane(){
        int countPokemon = 0;
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 4; j++){
                _anchor = new AnchorPane();
                _anchor.setPrefSize(width,height);
                Label nameLabel = new Label("  "+this.pokemonsList.get(countPokemon).getName());
                nameLabel.setStyle("-fx-translate-x: 20");
                ImageView imageView = new ImageView(new Image(Http.POKE_API_IMG+this.pokemonsList.get(countPokemon).getNumber()+".png"));
                imageView.setStyle("-fx-translate-x: 20;-fx-translate-y: 20");
                _anchor.getChildren().addAll(imageView, nameLabel);
                _anchor.setStyle("-fx-border-color: black;-fx-border-radius: 20px; -fx-alignment: center;-fx-column-halignment:center");
                _anchor.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        showPokemon(event);
                        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
                    }
                });
                gridPane.add(_anchor,j,i);
                countPokemon++;
            }
        }
    }

    private void clearGridPane(){
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
    }

    private void initGridPane(){
        clearGridPane();
        for (int i = 0; i < 5; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(width));
            if(i!=4) gridPane.getRowConstraints().add(new RowConstraints(height));
        }
    }

    private void getPokemons(){
        this.pokemons = Util.getPokemons(page);
        if(this.pokemons!=null)
            jsonToPokemon();
        else {
            JOptionPane.showMessageDialog(null, "No hay màs pokemos");
            this.pokemons = Util.getPokemons(page--);
        }
    }

    private void jsonToPokemon(){
        this.pokemonsList = new ArrayList<>();
        for (int i = 0; i < this.pokemons.length() ; i++)
            this.pokemonsList.add(new Pokemons(this.pokemons.getJSONObject(i)));
    }

    private void showPokemons(){
        getPokemons();
        initGridPane();
        insertPokemonToGridPane();
    }

    @FXML
    private void nextButton(ActionEvent event) {
        page++;
        showPokemons();
    }

    @FXML
    private void previousButton(ActionEvent event) {
        int _oldPage = page;
        if (page>1) page--;
        if(_oldPage!=page) showPokemons();
    }

    @FXML
    void searchButton(ActionEvent event) {
       findPokemonByName(event);
    }

    private void findPokemonByName(Event event){
        if(!nameTextField.getText().trim().isEmpty()){
            this.pokemon = Util.findPokemonByName(nameTextField.getText().trim());
            if (this.pokemon != null) {
                showViewPokemon();
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }
            else JOptionPane.showMessageDialog(null, "Pokemon no descubierto.");
        }
        else JOptionPane.showMessageDialog(null, "Por favor ingresar nombre");
    }

    @FXML
    void nameTextField(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER){
            findPokemonByName(event);
        }
    }


    @FXML private void initialize(){
        getPokemons();
        initGridPane();
        insertPokemonToGridPane();
    }
}
