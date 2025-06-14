package com.anshul.gen_ai.utility;

public class PromptUtility {

    public static String popularPlayersPrompt() {
        return "List of 5 most popular players in {sports}";
    }

    public static String popularPlayersInfoPrompt() {
        return "List of 5 most popular players in {sports}. {format}";
    }

    public static String popularPlayersDetailsPrompt() {
        return "5 best players in {sports}. {format}";
    }

    public static String explainImage() {
        return "Explain what do you see in this image";
    }
}
