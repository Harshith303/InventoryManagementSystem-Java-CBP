package inventory;

import java.util.Scanner;
import java.util.InputMismatchException;

class InputHandler{
    private static final Scanner sc = new Scanner(System.in);
    public static int getInt(String prompt){
        while(true){
            try{
                System.out.print(prompt);
                int a = sc.nextInt();
                sc.nextLine();
                return a;
            }
            catch(InputMismatchException ime){
                sc.nextLine();
                System.out.println("Invalid integer. Please try again.");
            }
        }
    }

    public static int getPositiveInt(String prompt){
        while(true){
            int value = getInt(prompt);
            if(value > 0){
                return value;
            }
            System.out.println("Value must be greater than zero.");
        }
    }

    public static int getNonNegativeInt(String prompt){
        while(true){
            int value = getInt(prompt);
            if(value >= 0){
                return value;
            }
            System.out.println("Value cannot be negative.");
        }
    }

    public static double getDouble(String prompt){
        while(true){
            try{
                System.out.print(prompt);
                double a = sc.nextDouble();
                sc.nextLine();
                return a;
            }
            catch(InputMismatchException ime){
                sc.nextLine();
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static double getPositiveDouble(String prompt){
        while(true){
            double value = getDouble(prompt);
            if(value > 0 && Double.isFinite(value)){
                return value;
            }
            System.out.println("Value must be a finite number greater than zero.");
        }
    }

    public static String getString(String prompt){
        while(true){
            System.out.print(prompt);
            String str = sc.nextLine();
            if(str.equals(""))continue;
            return str;
        }
    }
}
