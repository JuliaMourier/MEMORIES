package com.example.memories;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SelectedImages {
    public ArrayList<ImageFromStorage> imageList;
    public static String FILE_NAME = "foldersName.txt";
    public ArrayList<String> folderNameList = new ArrayList<String>();
    public void addSelectedImage(ImageFromStorage newImage){
        imageList.add(newImage);
    }
    public int getSelectedImagesNumber(){
        return imageList.size();
    }

    public void loadFolderNames(){
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line = reader.readLine()) != null)
            {
                folderNameList.add(line);
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
