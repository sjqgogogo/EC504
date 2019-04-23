import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class prob3 {
    public List<String> load(String filename, int wordsNum) {
        ArrayList<String> bigString = new ArrayList<>();
        FileInputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = new FileInputStream(filename);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while((str = bufferedReader.readLine()) != null)
            {
                bigString.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bigString.subList(0, wordsNum);
    }




    private static int min(int a, int b, int c) {
        if(a<=b)
            if(a<=c)
                return a;
            else
                return c;
        else
        if(b<=c)
            return b;
        else
            return c;
    }



    public static int distance(String a, String b) {

        int distance[][] = new int[a.length() + 1][b.length() + 1];
       for (int i = 0; i <= a.length(); i++)
           distance[i][0] = i;
        for (int j = 0; j <= b.length(); j++)
            distance[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    distance[i][j] = distance[i - 1][j - 1];
                } else {
                    distance[i][j] = 1 + min(distance[i-1][j-1],distance[i-1][j],distance[i][j-1]);
                }
            }
        }
        return distance[a.length()][b.length()];
    }



    private static int betweeni=0;
    private int maxDistance(List<String> key, HashMap<String, Integer> value) {
        int max = 0;
        int distance = 0;
        for(int i=0; i<key.size()-1;i++) {
            String str1 = key.get(i);
            String str2 = key.get(i+1);
            if(value.containsKey(str1+","+str2)) {
                distance = value.get(str1 + "," + str2);
                betweeni = i;
            }

            else if(value.containsKey(str2+","+str1))
                distance = value.get(str2+","+str1);
            //else
                //System.out.println("no such element:"+str1+","+str2+"  i="+i);
            if(distance>max) {
                max = distance;
                betweeni = i;
            }
        }
        return max;
    }



    public static void main(String[] args) {
        prob3 prob = new prob3();
        List<String> strList = prob.load("bigWords.txt",9);
        //System.out.println("strList="+strList);
        HashMap<String, Integer> distanceDict = new HashMap<>();
        for(int i=0;i<strList.size();i++)
            for(int j=i+1;j<strList.size();j++) {
                int editDistance = distance(strList.get(i),strList.get(j));
                distanceDict.put(strList.get(i)+","+strList.get(j),editDistance);
            }
        //System.out.println(strList);
        //System.out.println(distanceDict);
        int max=1000;
        List<String> result = new ArrayList<String>();
        for(int i=0;i<strList.size();i++) {
            int dis=0;
            max=1000;
            if(i<=1) {
                result.add(strList.get(i));
                continue;
            }
            //System.out.println("result="+result);
            List<String > temp = new ArrayList<String>();
            List<String > temp2 = new ArrayList<String>();
            for(int j=0;j<result.size();j++) {
                temp.clear();
                temp.addAll(result);
                temp.add(j,strList.get(i));
                dis = prob.maxDistance(temp,distanceDict);
                if(dis<max) {
                    max = dis;
                    temp2.clear();
                    temp2.addAll(temp);
                }
                //System.out.println(distanceDict);
                //dis = prob.maxDistance(temp,distanceDict);

            }
            result.clear();
            result.addAll(temp2);
        }
        for(int i=0;i<result.size();i++)
            System.out.println(result.get(i));
        System.out.println("maximum edit distance "+max+" between "+result.get(betweeni)+" and "+result.get(betweeni+1));


    }




}
