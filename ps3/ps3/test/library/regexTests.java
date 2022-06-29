package library;

class regexTests {
    public static void main(String[] args) {
        String word = "not \"hey man\"";
        String[] words = word.split("\"");
        
        for (String w : words) {
            System.out.println(w);
        }       
    }
}

