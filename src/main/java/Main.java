   public class Main {
        public static void main(String[] args) {
            System.out.println("Is 23 an Even Number?");
            System.out.println(checkIfInputIsAnEvenNumber(23));
        }

    public static boolean checkIfInputIsAnEvenNumber(int number){
        return number % 2 == 0;
    }
   }
