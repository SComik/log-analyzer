/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.Controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 *
 * @author Comik
 */
public class MainController implements Initializable
{

    @FXML
    private TextField tfPathCheck;

    String pathFile;
    File fileCheck;
    File fileWordList;
    String pathWordList;
    @FXML
    private TextField tfPathWordList;
    @FXML
    private TextArea taResult;
    @FXML
    private ProgressBar pbResult;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }

    @FXML
    private void eventLoadFile(ActionEvent event)
    {
        fileCheck = loadFile(tfPathCheck);

    }

    @FXML
    private void eventWordList(ActionEvent event)
    {
        fileWordList = loadFile(tfPathWordList);

    }

    @FXML
    private void eventCheck(ActionEvent event)
    {
        pbResult.setProgress(0);
        
        if (fileCheck != null && fileWordList != null)
        {
            String result = check(fileCheck, fileWordList);

            System.out.println(result);
            taResult.clear();
            taResult.setText(result);
        } else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Вы не выбрали файлы");
            alert.showAndWait();
        }
        
        pbResult.setProgress(1.0);
    }

    //загрузка файла
    File loadFile(TextField textField)
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
        {
            String path = file.toString();
            textField.setText(path);
        }

        return file;
    }

    //Проверка патернов с логами
    String check(File fileOne, File fileTwo)
    {
        StringBuilder report = new StringBuilder();

        try
        {

            BufferedReader bufferedReaderOne = new BufferedReader(new FileReader(fileTwo));
            ArrayList<String> patterns = new ArrayList<String>();
            String pattern;
            while ((pattern = bufferedReaderOne.readLine()) != null)
            {
                if(pattern.trim().isEmpty()) continue;
                patterns.add(pattern.toLowerCase().trim());
            }
            bufferedReaderOne.close();

            System.out.println("Загружено паттернов: " + patterns.size());
            for (String p : patterns)
            {
                
                System.out.println(" -" + p);
            }

            bufferedReaderOne = new BufferedReader(new FileReader(fileOne));
            
            int totalLines = 0;
            while (bufferedReaderOne.readLine() != null) totalLines++;
            bufferedReaderOne.close();

            bufferedReaderOne = new BufferedReader(new FileReader(fileOne));
            
            String logLine;
            int lineNum = 0;
            int processedLines = 0;

            while ((logLine = bufferedReaderOne.readLine()) != null)
            {
                lineNum++;
                processedLines++;
                
                double progress =  (double) processedLines / totalLines;
                javafx.application.Platform.runLater(() ->
                {
                    pbResult.setProgress(progress);
                });
                
                String lowerLogLine = logLine.toLowerCase();

                for (String p : patterns)
                {
                    if (lowerLogLine.contains(p))
                    {
                        String ip = logLine.split(" ")[0];
                        report.append("Строка ").append(lineNum).append(": Найден паттерн '").append(p).append("' | IP: ").append(ip).append("\n");
                        report.append("   ").append(logLine.substring(0, Math.min(150, logLine.length()))).append("\n\n");
                        break;
                    }
                }
            }

            bufferedReaderOne.close();

        } catch (IOException e)
        {
            System.out.println(e);

        }

        if (report.length() == 0)
        {
            return "Подозрительность не найдена";
        }

        return report.toString();
    }
}
