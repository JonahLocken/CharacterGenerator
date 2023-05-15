package org.example;

import java.util.List;

public class Character {
    public String name;
    public String height;
    public String weight;
    public String race;
    public String raceInformation;
    public String characterClass;
    public List<String> classInformation;
    public String subtree;
    public List<String> subtreeInformation;
    public int[] statistics;

    public Character(String name, String height, String weight, String race, String raceInformation, String characterClass, List<String> classInformation, String subtree, List<String> subtreeInformation, int[] statistics) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.race = race;
        this.raceInformation = raceInformation;
        this.characterClass = characterClass;
        this.classInformation = classInformation;
        this.subtree = subtree;
        this.subtreeInformation = subtreeInformation;
        this.statistics = statistics;
    }
}
