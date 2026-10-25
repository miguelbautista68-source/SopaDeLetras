package org.uabc.ic.sopainterfaz;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.uabc.ic.sopainterfaz.Controlador.Controlador;
import org.uabc.ic.sopainterfaz.modelo.Tablero;

public class VistaJavaFX extends Application {

    private static final int FILAS_FIJAS = 20;
    private static final int COLUMNAS_FIJAS = 20;

    private Controlador controlador;
    private GridPane gridTablero;
    private Label lblEstado;
    private TextField txtRutaGuardar;
    private TextField txtPalabraBuscar;

    @Override
    public void start(Stage primaryStage) {
        controlador = new Controlador();

        primaryStage.setTitle("Sopa de Letras - JavaFX");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Encabezado superior
        VBox topBox = new VBox(10);
        Label lblTitulo = new Label("SOPA DE LETRAS");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        lblEstado = new Label("Ingresa un nombre al texto y despues busca una palabra");
        lblEstado.setStyle("-fx-font-size: 13px; -fx-text-fill: #2c3e50;");
        topBox.getChildren().addAll(lblTitulo, lblEstado);
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        // Cuadrícula central de la sopa de letras
        gridTablero = new GridPane();
        gridTablero.setAlignment(Pos.CENTER);
        gridTablero.setHgap(3);
        gridTablero.setVgap(3);
        gridTablero.setPadding(new Insets(15));

        ScrollPane scrollTablero = new ScrollPane(gridTablero);
        scrollTablero.setFitToWidth(true);
        scrollTablero.setFitToHeight(true);
        root.setCenter(scrollTablero);

        // Panel lateral izquierdo de controles
        VBox panelControles = new VBox(12);
        panelControles.setPadding(new Insets(12));
        panelControles.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        panelControles.setPrefWidth(260);

        Label lblSeccion1 = new Label("1. Generar Tablero");
        lblSeccion1.setStyle("-fx-font-weight: bold;");

        txtRutaGuardar = new TextField("sopa.txt");
        txtRutaGuardar.setPromptText("Archivo de salida");

        Button btnGenerar = new Button("Generar y Guardar");
        btnGenerar.setMaxWidth(Double.MAX_VALUE);
        btnGenerar.setOnAction(e -> accionGenerar());

        Label lblSeccion2 = new Label("2. Buscar Palabra");
        lblSeccion2.setStyle("-fx-font-weight: bold;");

        txtPalabraBuscar = new TextField();
        txtPalabraBuscar.setPromptText("Ej. ferrocarril");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setMaxWidth(Double.MAX_VALUE);
        btnBuscar.setOnAction(e -> accionBuscar());

        panelControles.getChildren().addAll(
                lblSeccion1,
                new Label("Guardar en:"), txtRutaGuardar,
                btnGenerar,
                new Separator(),
                lblSeccion2,
                txtPalabraBuscar,
                btnBuscar
        );

        root.setLeft(panelControles);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void accionGenerar() {
        try {
            String ruta = txtRutaGuardar.getText().trim();
            Tablero tablero = controlador.generarTablero(FILAS_FIJAS, COLUMNAS_FIJAS, ruta);
            actualizarGridVisual(tablero);

            String msj = "La sopa de letras se ha generado segun: " + ruta;
            if (!controlador.getNoColocadas().isEmpty()) {
                msj += " | No cupieron: " + controlador.getNoColocadas();
            }
            lblEstado.setText(msj);
        } catch (Exception e) {
            lblEstado.setText("Error al generar: " + e.getMessage());
        }
    }

    private void accionBuscar() {
        String palabra = txtPalabraBuscar.getText().trim();
        if (palabra.isEmpty()) {
            lblEstado.setText("Escribe la palabra a buscar.");
            return;
        }

        try {
            String resultado = controlador.buscarPalabra(palabra);
            lblEstado.setText(resultado);
        } catch (Exception e) {
            lblEstado.setText("Error: " + e.getMessage());
        }
    }

    private void actualizarGridVisual(Tablero tablero) {
        gridTablero.getChildren().clear();

        // Esquina superior izquierda vacía
        Label cellEsquina = new Label("");
        cellEsquina.setPrefSize(26, 26);
        gridTablero.add(cellEsquina, 0, 0);

        // Encabezados superiores de Columnas (1 a 20)
        for (int j = 0; j < tablero.getCoordenadaB(); j++) {
            Label headerCol = new Label(String.valueOf(j + 1));
            headerCol.setPrefSize(26, 26);
            headerCol.setAlignment(Pos.CENTER);
            headerCol.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
            gridTablero.add(headerCol, j + 1, 0);
        }

        // Encabezados laterales de Filas (1 a 20) y celdas con letras
        for (int i = 0; i < tablero.getCoordenadaA(); i++) {
            Label headerRow = new Label(String.valueOf(i + 1));
            headerRow.setPrefSize(26, 26);
            headerRow.setAlignment(Pos.CENTER);
            headerRow.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
            gridTablero.add(headerRow, 0, i + 1);

            for (int j = 0; j < tablero.getCoordenadaB(); j++) {
                char letra = tablero.getSopa(i, j).getLetra();

                Label cell = new Label(String.valueOf(letra).toUpperCase());
                cell.setPrefSize(26, 26);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-border-color: #bdc3c7; -fx-background-color: #ffffff; -fx-font-weight: bold; -fx-font-size: 11px;");

                gridTablero.add(cell, j + 1, i + 1);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}