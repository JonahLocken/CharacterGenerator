package org.example;


import java.util.Arrays;
import java.util.List;

public class Character {
    private String name;
    private String height;
    private String weight;
    private String race;
    private String raceInformation;
    private String characterClass;
    private List<String> classInformation;
    private String subtree;
    private List<String> subtreeInformation;
    private int[] statistics;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getRaceInformation() {
        return raceInformation;
    }

    public void setRaceInformation(String raceInformation) {
        this.raceInformation = raceInformation;
    }

    public String getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    public List<String> getClassInformation() {
        return classInformation;
    }

    public void setClassInformation(List<String> classInformation) {
        this.classInformation = classInformation;
    }

    public String getSubtree() {
        return subtree;
    }

    public void setSubtree(String subtree) {
        this.subtree = subtree;
    }

    public List<String> getSubtreeInformation() {
        return subtreeInformation;
    }

    public void setSubtreeInformation(List<String> subtreeInformation) {
        this.subtreeInformation = subtreeInformation;
    }

    public int[] getStatistics() {
        return statistics;
    }

    public void setStatistics(int[] statistics) {
        this.statistics = statistics;
    }

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

    public java.lang.String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getName()).append("\n");
        sb.append("Height: ").append(getHeight()).append("\n");
        sb.append("Weight: ").append(getWeight()).append("\n");
        sb.append("Race: ").append(getRace()).append("\n");
        sb.append("Race Information: ").append(getRaceInformation()).append("\n");
        sb.append("Character Class: ").append(getCharacterClass()).append("\n");
        sb.append("Subtree: ").append(getSubtree()).append("\n");
        sb.append("Subtree Information: ").append(getSubtreeInformation()).append("\n");
        sb.append("Statistics: ").append(Arrays.toString(getStatistics())).append("\n");
        return sb.toString();
    }

}
