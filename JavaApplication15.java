/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication15;

import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author ntluo
 */
public class JavaApplication15 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner s = new Scanner(System.in);       
        int n = s.nextInt();
        
        int b =0;
        int j =0;
        boolean flag = false;
        if(check(n) ==true)
        {
          b=0;
        }
        else
        {  
            for(j = 0;j < n; j++)
            {   
                if(checkList(n) ==true)
                {
                  System.out.println(checkList(n));
                   break; 
                }

            }                
        }
               System.out.println(b);

    }
    public static int CongTru(int a,int b)
    {
        int kq =0;
        kq =a+1;
        if(check(kq) == true){return 1;}
        kq =0;
        kq =a+b;
        if(check(kq)== true){return 1;} 
        kq =0;
        kq =a-1;
        if(check(kq) == true){return 1;}
        kq =0;
        kq =a-b;
        if(check(kq)== true){return 1;}
        return 0;
    }
     
public static boolean check(int n) {
        // so nguyen n < 2 khong phai la so nguyen to
        if (n < 2) {
            return false;
        }        
        // check so nguyen to khi n >= 2
        int squareRoot = (int) Math.sqrt(n);
        for (int i = 2; i <= squareRoot; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean checkList(int n)
    {
         int chuoi = n;
        int i;
        List<Integer> list = new ArrayList<>();
        while (chuoi > 0)
        {
           i= chuoi % 10;
           list.add(i);
           chuoi = chuoi / 10;
        }  
        Collections.sort(list);
        for(int z =0;z<list.size();z++)
        {                          
         if(CongTru(n,list.get(z))==1)
            {
                return true;
            }                     
        }
        return false;
        
    }

}
