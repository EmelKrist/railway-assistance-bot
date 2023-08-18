package ru.emelkrist.utils;

public class CityUtils {

    /**
     * Method for formatting city name.
     * Note: format of city name is "Word1 Word2 Word3..."
     *
     * @param cityName name of city
     * @return formatted name of city
     */
    public static String formatCityName(String cityName) {
        StringBuilder formattedCityName = new StringBuilder(cityName
                // remove extra spaces and convert to lowercase
                .trim()
                .replaceAll("\\s{2,}", " ")
                .toLowerCase());
        // all first letters of each word are converted to uppercase
        for (int i = 0; i < formattedCityName.length(); i++) {
            if (i == 0 || formattedCityName.charAt(i - 1) == ' ')
                formattedCityName.setCharAt(i,
                        Character.toUpperCase(formattedCityName.charAt(i)));
        }

        return formattedCityName.toString();
    }
}
