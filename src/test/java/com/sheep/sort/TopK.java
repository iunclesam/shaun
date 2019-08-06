package com.sheep.sort;

import javax.sound.midi.Soundbank;
import java.util.Arrays;

public class TopK {
    private static final int[] array = new int[]{7,1,8,3,7,9,6,3,4,5,0,2};

    public static void main(String[] args) {
        int[] sorted = bubbleK(array, 2);
        System.out.println(Arrays.toString(sorted));
    }

    public static int[] bubbleK(int[] target, int k) {
        for (int i = 0; i < k; i++) {
            for (int j = target.length - 1; j > i; j--){
                if(target[j] > target[j-1]){
                    int temp = target[j];
                    target[j] = target[j-1];
                    target[j-1] = temp;
                }
            }
            System.out.println(Arrays.toString(target) + i);
        }
        return target;
    }
}
