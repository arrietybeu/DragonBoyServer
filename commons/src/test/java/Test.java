import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        int n;

        int[] a = new int[200];

        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();

        for(int i = 0; i < n; i++){
            a[i] = scanner.nextInt();
        }
        int combine = 0;
        for(int i = 0; i < n; i++){
            combine += a[i];
        }
        System.out.print(combine);
    }
}