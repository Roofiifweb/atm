package com.example.demo1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HelloController extends Application {

    private static final String USER_FILE = "pengguna.txt";
    private static Map<String, String> userData; // Map untuk menyimpan data pengguna (PIN -> Nama Pengguna)
    private static double saldo = 4000000.0;

    public static void main(String[] args) {
        loadUserData();
        launch(args);
    }

    private static void loadUserData() {
        userData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    userData.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("ATM Bank Application");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 50, 50, 50));

        Label welcomeLabel = new Label("Selamat datang di ATM");
        vbox.getChildren().add(welcomeLabel);

        // PIN Input
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("Masukkan PIN Anda");
        vbox.getChildren().add(pinField);

        // Nama Pengguna Input
        TextField usernameField = new TextField();
        usernameField.setPromptText("Masukkan Nama Pengguna Anda");
        vbox.getChildren().add(usernameField);

        // Buttons
        Button checkBalanceButton = new Button("Cek Saldo");
        Button withdrawButton = new Button("Tarik Tunai");
        Button depositButton = new Button("Setor Tunai");
        Button printReceiptButton = new Button("Cetak Struk");

        vbox.getChildren().addAll(checkBalanceButton, withdrawButton, depositButton, printReceiptButton);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();

        // Event Handlers
        checkBalanceButton.setOnAction(e -> cekSaldo(pinField.getText()));
        withdrawButton.setOnAction(e -> tarikTunai(pinField.getText()));
        depositButton.setOnAction(e -> setorTunai(pinField.getText()));
        printReceiptButton.setOnAction(e -> cetakStruk(pinField.getText()));
    }

    private static void cekSaldo(String pin) {
        if (validateUser(pin)) {
            showAlert("Saldo Anda: Rp" + saldo);
        }
    }

    private static void tarikTunai(String pin) {
        if (validateUser(pin)) {
            double jumlah = promptAmount("Masukkan jumlah uang yang ingin ditarik");

            if (jumlah > saldo || jumlah <= 0) {
                showAlert("Jumlah penarikan tidak valid atau saldo tidak mencukupi.");
            } else {
                saldo -= jumlah;
                showAlert("Penarikan berhasil. Saldo tersisa: Rp" + saldo);
            }
        }
    }

    private static void setorTunai(String pin) {
        if (validateUser(pin)) {
            double jumlah = promptAmount("Masukkan jumlah uang yang ingin disetor");

            if (jumlah <= 0) {
                showAlert("Jumlah setoran tidak valid.");
            } else {
                saldo += jumlah;
                showAlert("Setoran berhasil. Saldo baru: Rp" + saldo);
            }
        }
    }

    private static void cetakStruk(String pin) {
        if (validateUser(pin)) {
            try (PrintWriter writer = new PrintWriter(new FileWriter("struk.txt"))) {
                writer.println("Struk Transaksi");
                writer.println("Tanggal: " + java.time.LocalDate.now());
                writer.println("Waktu: " + java.time.LocalTime.now());
                writer.println("Detail Transaksi:");
                writer.println("Jumlah yang Ditarik: Rp" + (1000.0 - saldo));
                writer.println("Saldo Tersisa: Rp" + saldo);
                showAlert("Struk berhasil dicetak.");
            } catch (IOException e) {
                showAlert("Error mencetak struk: " + e.getMessage());
            }
        }
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static double promptAmount(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Masukkan Jumlah");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);

        try {
            return Double.parseDouble(dialog.showAndWait().orElse("0"));
        } catch (NumberFormatException e) {
            showAlert("Masukkan jumlah yang valid.");
            return 0;
        }
    }

    private static boolean validateUser(String pin) {
        String username = userData.get(pin);
        if (username != null) {
            showAlert("Selamat datang, " + username + "!");
            return true;
        } else {
            showAlert("PIN atau Nama Pengguna salah.");
            return false;
        }
    }
}
