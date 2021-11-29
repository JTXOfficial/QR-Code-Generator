package dev.jtx.qrCodeGenerator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;



public class QRCodeGenerator extends Application {

    static final int GAP = 15;
    static final int LARGE_FONT = 40;
    static final int MEDIUM_FONT = 25;
    static final int SMALL_FONT = 12;

    File file;

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();

        root.setVgap(GAP);
        root.setHgap(GAP);
        root.setPadding(new Insets(GAP, GAP, GAP, GAP));

        Label lblTitle = new Label("WiFi QR Generator");
        lblTitle.setFont(Font.font(LARGE_FONT));
        root.add(lblTitle, 1,0);

        Label lblNetwork = new Label("Network Name");
        lblNetwork.setFont(Font.font(MEDIUM_FONT));
        root.add(lblNetwork, 0, 1);

        TextField txtNetwork = new TextField();
        txtNetwork.setPromptText("SSID");
        root.add(txtNetwork, 1, 1);

        Label lblPassword = new Label("Password");
        lblPassword.setFont(Font.font(MEDIUM_FONT));
        root.add(lblPassword, 0, 2);

        PasswordField psfPassword = new PasswordField();
        root.add(psfPassword, 1, 2);

        Label lblEncryption = new Label("Encryption");
        lblEncryption.setFont(Font.font(MEDIUM_FONT));
        root.add(lblEncryption, 0, 4);

        ToggleGroup group = new ToggleGroup();

        RadioButton rbEncryptionNone = new RadioButton("None");
        rbEncryptionNone.setSelected(true);
        rbEncryptionNone.setToggleGroup(group);
        root.add(rbEncryptionNone, 1, 4);

        RadioButton rbEncryptionWPA = new RadioButton("WPA/WPA2");
        rbEncryptionWPA.setToggleGroup(group);
        root.add(rbEncryptionWPA, 2, 4);

        RadioButton rbEncryptionWEP = new RadioButton("WEP");
        rbEncryptionWEP.setToggleGroup(group);
        root.add(rbEncryptionWEP, 3, 4);

        Label lblFile = new Label("Directory");
        lblFile.setFont(Font.font(MEDIUM_FONT));
        root.add(lblFile,0, 5);

        TextField txtPath = new TextField();
        txtPath.setFont(Font.font(SMALL_FONT));
        txtPath.setEditable(false);
        root.add(txtPath, 1, 5);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Location");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*gif"));

        Label lblResponse = new Label();
        lblResponse.setFont(Font.font(SMALL_FONT));
        root.add(lblResponse, 0, 7);

        Button btnSaveFile = new Button("Select Location");
        btnSaveFile.setOnAction(event ->  {
            file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) txtPath.setText(file.getPath());
        });

        root.add(btnSaveFile, 2, 5);

        Button btnGenerate = new Button("Generate");
        root.add(btnGenerate, 1, 6);
        btnGenerate.setOnAction(event -> {
            if (file != null) {
                if (!lblNetwork.getText().isEmpty()) {
                    if (!psfPassword.getText().isEmpty()) {
                        if (rbEncryptionWPA.isSelected()) {
                            try {
                                generate(file.getPath(), txtNetwork.getText(), psfPassword.getText());
                                lblResponse.setText("QR Code created successfully.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        } else if (rbEncryptionNone.isSelected()) {
                            lblResponse.setText("Sorry, option this isn't currently available.");
                        } else if (rbEncryptionNone.isSelected()) {
                            lblResponse.setText("Sorry, this option isn't currently available.");
                        }
                    }
                }
            } else lblResponse.setText("File Location not selected. Please restart application!");
        });

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Qr Code Generator");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void generate(String path, String ssid, String password) throws IOException, WriterException {
        String wifiString = "WIFI:S:%s;T:WPA;P:%s;;";
        wifiString = String.format(wifiString, ssid, password);

        MultiFormatWriter writer = new MultiFormatWriter();
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        // Image has to be big enough to print onto A4 in reasonable quality
        BitMatrix bitMatrix = writer.encode(wifiString, BarcodeFormat.QR_CODE, 1600, 1600, hints);
        MatrixToImageWriter.writeToFile(bitMatrix, path.substring(path.lastIndexOf('.') + 1), new File(path));
    }

}
