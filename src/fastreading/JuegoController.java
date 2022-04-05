/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package fastreading;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Pincay
 */
public class JuegoController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private static String texto;
    private String lineaActual;
    private Queue<String> cola;

    @FXML
    private RadioButton rbManual;
    @FXML
    private ToggleGroup opciones;
    @FXML
    private RadioButton rbTemp;
    @FXML
    private Spinner<Integer> spinPalabras;
    private int valorActualPalabras;
    @FXML
    private Spinner<Integer> spinMilisegundos;
    private int valorActualMilisegundos;
    @FXML
    private Button btnEmpezar;
    @FXML
    private Button btnTerminar;
    @FXML
    private Label lblTiempo;
    @FXML
    private Label lblOracion;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnReiniciar;
    @FXML
    private ProgressBar progresLectura;
    private float progress;
    private float incremento;
    private Timer crono = new Timer();
    private int segundos;
    private int totalPalabras;
    @FXML
    private Label lblTiempo2;
    @FXML
    private Text txtResultados;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // stage.setResizable(false);
        creadorSpinnerPalabras();
        creadorSpinnerMilisegundos();

    }

    public void creadorSpinnerPalabras() {
        SpinnerValueFactory<Integer> valueFactory
                = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);
        valueFactory.setValue(1);
        spinPalabras.setValueFactory(valueFactory);
        valorActualPalabras = spinPalabras.getValue();
        spinPalabras.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> arg0, Integer arg1, Integer arg2) {
                valorActualPalabras = spinPalabras.getValue();
            }
        });

    }

    public void creadorSpinnerMilisegundos() {
        SpinnerValueFactory<Integer> valueFactory
                = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);
        valueFactory.setValue(1);
        spinMilisegundos.setValueFactory(valueFactory);
        valorActualMilisegundos = spinMilisegundos.getValue();
        spinMilisegundos.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> arg0, Integer arg1, Integer arg2) {
                valorActualMilisegundos = spinMilisegundos.getValue();
            }
        });

    }

    @FXML
    public void setEnable(ActionEvent ae) {
        if (rbManual.isSelected()) {
            spinMilisegundos.setDisable(true);
        } else if (rbTemp.isSelected()) {
            spinMilisegundos.setDisable(false);
        }
    }

    @FXML
    private void empezarJuego(ActionEvent event) throws InterruptedException {
        crearCola();
        txtResultados.setText("");
        btnEmpezar.setDisable(true);
        crearLinea();
        Timer tm = new Timer();
        tm.scheduleAtFixedRate(new TimerTask() {
            int i = 3;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    lblTiempo.setText(String.valueOf(i));
                    i--;
                    if (i < 0) {
                        tm.cancel();
                        lblTiempo.setText("¡YA!");
                        btnSiguiente.setDisable(false);
                        btnTerminar.setDisable(false);
                        mostrarLinea();
                        crono = new Timer();
                        crono.scheduleAtFixedRate(new TimerTask() {
                            int i = 0;

                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    lblTiempo2.setText(String.valueOf(i));
                                    i++;
                                    if (progress >= 0.99999f) {
                                        crono.cancel();
                                        segundos = Integer.parseInt(lblTiempo2.getText());
                                        mostrarResultado();
                                    }
                                });
                            }
                        }, 0, 1000);
                    }
                });
            }
        }, 0, 1000);

    }

    @FXML
    private void terminarJuego(ActionEvent event) {
        if (rbManual.isSelected()) {
            crono.cancel();
            segundos = Integer.parseInt(lblTiempo2.getText());
            mostrarResultado();
        }
        if (rbTemp.isSelected()) {
        }
        btnReiniciar.setDisable(false);
        btnSiguiente.setDisable(true);
        btnTerminar.setDisable(true);
    }

    @FXML
    private void siguienteOracion(ActionEvent event) {
        if (lblTiempo.getText().length() != 0) {
            lblTiempo.setText("");
        }
        //valorActualPalabras
        progress = progress + incremento;
        progresLectura.setProgress(progress);
        if (!cola.isEmpty()) {
            crearLinea();
            mostrarLinea();
        } else {
            //mostrar el resultado
            btnSiguiente.setDisable(true);
            btnReiniciar.setDisable(false);
            btnTerminar.setDisable(true);
        }

    }

    @FXML
    private void volver(ActionEvent event) throws IOException {
        crono.cancel();
        root = FXMLLoader.load(getClass().getResource("Inicio.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void reiniciar(ActionEvent event) {
        progress = 0;
        incremento = 0;
        crearCola();
        btnEmpezar.setDisable(false);
        btnTerminar.setDisable(true);
        btnReiniciar.setDisable(true);
        progresLectura.setProgress(0);
        lblTiempo2.setText("");
        lblOracion.setText("");

    }

    public void mostrarResultado() {
        float minutos = dividir(Integer.parseInt(lblTiempo2.getText()),60);
        float division = dividir(totalPalabras-cola.size(), minutos);
        txtResultados.setText("Resultado\n" +division+" P/m");
    }

    public void crearCola() {
        String textoLineal = texto.replace('\n', ' ');
        String[] lineas = textoLineal.split(" ");
        cola = new ArrayDeque<>();
        for (String s : lineas) {
            cola.offer(s);
        }
        totalPalabras = cola.size();
        double total = dividir(totalPalabras, valorActualPalabras);
        double total2 = Math.ceil(total);
        incremento = dividir(1, (float) total2);

    }

    public float dividir(float num1, float num2) {
        float resultado = num1 / num2;
        return resultado;
    }

    public void crearLinea() {
        lineaActual = "";
        for (int i = 0; i < valorActualPalabras; i++) {
            if (!cola.isEmpty()) {
                if (i == valorActualPalabras - 1) {
                    lineaActual = lineaActual + cola.poll();
                } else {
                    lineaActual = lineaActual + cola.poll() + " ";
                }
            } else {
                break;
            }
        }
    }

    public void mostrarLinea() {
        lblOracion.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        lblOracion.setStyle("-fx-font-weight: bold");
        lblOracion.setText(lineaActual);

    }

    public static String getTexto() {
        return texto;
    }

    public static void setTexto(String texto) {
        JuegoController.texto = texto;
    }

}
