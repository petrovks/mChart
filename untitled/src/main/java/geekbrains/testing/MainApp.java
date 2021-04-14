package geekbrains.testing;

import java.util.ArrayList;
import java.util.List;

public class MainApp {
    public static int[] searchNumbers(int[] arr){
        int a = arr.length;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == 4) {
                a = i;
                break;
            }
        }
        int[] arr1 = new int[arr.length - 1 - a];
        System.arraycopy(arr,a+1, arr1,0,arr1.length);
        return arr1;
    }

    public static boolean checkingOneAndFour(int[] arr){
        for (Integer a: arr) {
            if (a == 1 || a == 4) return true;
        }
        return false;
    }
}
