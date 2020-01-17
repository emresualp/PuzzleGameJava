/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ylabpuzzle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Emre
 */
public class puzzleFile {

    File file;

    public puzzleFile() {

    }

    public puzzleFile(String str) throws IOException {
        file = new File("highScore.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file, true);
        try {
            try (BufferedWriter bWriter = new BufferedWriter(fileWriter)) {
                bWriter.write(str);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    public double getHighScore() throws FileNotFoundException, IOException {
        file = new File("highScore.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileReader fileReader = new FileReader(file);
        String line;
        String arr[];
        double highScore = 0.0;
        try (BufferedReader br = new BufferedReader(fileReader)) {
            while ((line = br.readLine()) != null) {
                arr = line.split(":");
                if (highScore < Double.parseDouble(arr[1])) {
                    highScore = Double.parseDouble(arr[1]);
                }
            }
        }

        return highScore;

    }
}
