import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class LocalitySensitiveHashing {
	
	static int[][] minhashMatrix ;
	static int[][] hashMatrix ;
	static double [][] jaccardMatrix ;
	static int totalNodes;
	static int numofhashfunctions = 12;
	static int numofbands = 3 ; // b = bands
	static int numofrows = 4; // r = rows 
	static int buckets = 100;   // hashtable buckets
	static Map<Integer, List<Integer>> [] map = new HashMap[numofbands];
	
	//static Hashtable<Integer, List<Integer> > [] hashtable =  new Hashtable [numofbands]; 
	
	
	public static void InitailizeMatrix(int [][] matrix) 
	{
		for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
            	matrix[i][j] = 1000;
            }
        }
		
	}
	
	public static void PrintMatrix(int [][] matrix)
	{
		System.out.println();
		for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
            	System.out.print(matrix[i][j]);
            	System.out.print(" ");
            }
            System.out.println();
            System.out.println();
        }
		System.out.println();
	}
	
	
	
	public static int computeTotatNodes()
	{
		BufferedReader reader;
		int result = 0;
		try {
			reader = new BufferedReader(new FileReader("C:/Users/Administrator/Desktop/text/text.txt"));
			String line = reader.readLine();
			while (line != null) {
				result ++ ;
				line = reader.readLine();
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static void readfile(int [][] hashMatrix) {
		
				for (int j = 1; j <=totalNodes  ; j++) 
				{	
					for (int i = 0; i < numofhashfunctions ; i++) 
					{
						int hashvalue = RandomFunction(j,i);
						hashMatrix[i][j - 1] = hashvalue;
						
						
					}  
				}
		             
		
	}
	
	public static void ComputeMinhashMatrix(int [][] hashMatrix,int [][] minhashMatrix,int totalnodes) {
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("C:/Users/Administrator/Desktop/text/text.txt"));
			String line = reader.readLine();
			int x = 0;
			while (line != null) {
				String[] SeperateNodesFromNeighbor=line.split(":");
				String[] neighbour = SeperateNodesFromNeighbor[1].split(",");
				
				for (String num : neighbour)  
		        { 
					    int neighbournode = Integer.parseInt(num);
						for (int i = 0; i < numofhashfunctions ; i++) 
						{
							
							int minhashvalue = minhashMatrix[i][x];
							int hashvalue = hashMatrix[i][neighbournode - 1];
							if(hashvalue < minhashvalue)
							{
								minhashMatrix[i][x] = hashMatrix[i][neighbournode - 1];
							}
							
						} 
		        } 
				// read next line
				line = reader.readLine();
				x++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void ComputeCandidatePairs(int [][] minhashMatrix,int totalnodes) 
	{
		for (int i = 0; i < numofbands ; i++) 
		{
			for (int columns = 0  ; columns< totalNodes ;columns++)
			{
				Set<Integer> node = new HashSet<Integer>();
				for (int row =(i)*numofrows ; row<(i+1)*numofrows ;row++)
				{
					node.add(minhashMatrix[row][columns]);
				    
				}
				Integer bucket = BucketHashFunction(node);
				if (map[i] == null) {
					map[i] = new HashMap();
				}
				
				
				if(map[i].containsKey(bucket))
				{
					map[i].get(bucket).add(columns + 1);
				}
				else
				{
					List<Integer> list = new ArrayList<>();
					map[i].put(bucket, list);
					map[i].get(bucket).add(columns + 1);
					
				}
			}
		}
	}
	
	
	public static void ComputerjaccardMatrix(double [][] jaccardMatrix)
	{

		for (int columns = 0  ; columns< totalNodes ;columns++)
		{
			Set<Integer> node = new HashSet<Integer>();
			Set<Integer> othernode = new HashSet<Integer>();
			
			for (int row = 0 ; row< numofhashfunctions ;row++)
			{
				//System.out.println("Column is " + columns + " row is " + row+ " Adding " + minhashMatrix[row][columns]);
				node.add(hashMatrix[row][columns]);
			    
			}
			for (int c = 0 ; c < totalNodes ;c++)
			{
				for (int row = 0 ; row< numofhashfunctions ;row++)
				{
					//System.out.println("Column is " + columns + " row is " + row+ " Adding " + minhashMatrix[row][columns]);
					othernode.add(hashMatrix[row][columns]);
				    
				}
				jaccardMatrix[columns][c] =  ComputerJaccard(node,othernode);
				
			}
			
			
		
		}
        
		
	}
	
	public static int BucketHashFunction(Set<Integer> nodebucketdata)
	{
		int sumint = 0;
		
		for(Integer i : nodebucketdata)
		{
			Random rand = new Random();
		    int n = rand.nextInt(buckets);
			//System.out.println(n);
			sumint = (sumint + i );//  % (n + 1);
		}
		
		 return sumint  % buckets;
	}
	
	public static Integer RandomFunction(int nodeNumber , int number)
	{
		 return (2 * nodeNumber + number*2  ) % totalNodes ;
	}
	
	
	public static double ComputerJaccard(Set<Integer> s1 , Set <Integer> s2)
	{
		Set<Integer> union = new HashSet<>(s1);
		union.addAll(s2);//Union
		Set<Integer> intersection = new HashSet<>(s1);
		intersection.addAll(s2);//Intersection
		return intersection.size() / union.size();
	}
	
	
	
	public static void ShowHashMap(Map<Integer, List<Integer>> [] map) {
		
		for (int h = 0  ; h < map.length ;h++)
		{
			
			System.out.println(map[h]);
			/*
			for ( Map.Entry<Integer, List<Integer>> entry : map[hashmap].entrySet()) {
		        Integer key = entry.getKey();
		        List<Integer> lists = entry.getValue();
		        System.out.println(lists);
		        
		        // do something with key and/or tab
		    }
		    */
		}
	}
	
	public static void main(String[] args) {
		totalNodes = computeTotatNodes();
		System.out.println(totalNodes);
		hashMatrix = new int[numofhashfunctions][totalNodes];
		InitailizeMatrix(hashMatrix);
		readfile(hashMatrix);
		
		System.out.println(" || Printing Hash Matrix || ");
		PrintMatrix(hashMatrix);
		System.out.println(" || Done Printing Hash Matrix || ");
		System.out.println();
		
		minhashMatrix = new int[numofhashfunctions][totalNodes];
		InitailizeMatrix(minhashMatrix);
	    //System.out.println("Printing Nodes");
		ComputeMinhashMatrix(hashMatrix,minhashMatrix,totalNodes);
		System.out.println(" || Printing Min Hash Matrix || ");
		PrintMatrix(minhashMatrix);
		System.out.println(" || Done Printing Min Hash Matrix || ");
		System.out.println();
		
	    ComputeCandidatePairs(minhashMatrix,totalNodes);
	    
	    
		System.out.println(" || Printing HashMaps || ");
		ShowHashMap(map);
		System.out.println(" || Done Printing HashMaps || ");
		System.out.println();
	    
	}
	
	
	
	
	
	
	
	
}
