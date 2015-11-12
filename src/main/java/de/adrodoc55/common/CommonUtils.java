package de.adrodoc55.common;

public class CommonUtils {

    public static String multiReplace(String string, char[] oldChars, char[] newChars) {
        char[] oldCharArray = string.toCharArray();
        char[] newCharArray = new char[oldCharArray.length];
        outer: for (int a = 0; a < oldCharArray.length; a++) {
            for (int b = 0; b < oldChars.length; b++) {
                if (oldCharArray[a] == oldChars[b]) {
                    newCharArray[a] = newChars[b];
                    continue outer;
                }
            }
            newCharArray[a] = oldCharArray[a];
        }
        return new String(newCharArray);
    }

}
