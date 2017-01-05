package createids;

import java.util.*;

public class CreateIDs {

    public static void main(String[] args) {
        Random rand = new Random();
        int i = 0;
        int x = 10;
        int[] ID = new int[x];
        while (i < x) {
            ID[i] = rand.nextInt(99999);
            for (int j = 0; j < i; j++) {
                if (ID[i] == ID[j]) {
                    continue;
                }
            }
            System.out.println(ID[i]);
            i++;
        }
    }

}
