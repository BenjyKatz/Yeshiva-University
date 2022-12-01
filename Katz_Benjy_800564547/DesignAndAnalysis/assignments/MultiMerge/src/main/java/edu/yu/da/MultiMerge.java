package edu.yu.da;

public class MultiMerge extends MultiMergeBase{
    public MultiMerge(){
        super();
    }
   /* @Override
    public void combinedAMerge() {
       super().combinedAMerge();
      }
    @Override
      public int getNCombinedMerges() {
        super();
      }*/
    
    @Override
    public int[] merge(final int[][] arrays){
       int[][] arrayz = arrays;
      // super.getNCombinedMerges();
        while(true){
           /* for(int i = 0; i< arrayz.length; i++){
                for(int j = 0; j<arrayz[i].length; j++){
                    System.out.print(arrayz[i][j]+" ");
                }
                System.out.println();
            }*/
            if(arrayz.length == 1) return arrayz[0];
            
           // System.out.flush();
            int amountOfArrays = (int)Math.ceil(((double)arrayz.length)/2);
            
            int[][] aux = new int[amountOfArrays][arrayz[0].length*2];
            int iterations = arrayz.length/2;
            for(int i = 0; i<iterations*2; i= i+2){
                aux[i/2] = combine(arrayz[i], arrayz[i+1]);
            }
            if(arrayz.length%2 == 1){  //the array has an odd amount of elements
      //          System.out.println("Adding the last sorted array with size: "+ arrayz[arrayz.length-1].length);
                aux[aux.length-1] = arrayz[arrayz.length-1];//add the last sorted array
         //       System.out.println("New arraysize: "+ aux[aux.length-1].length);
            }
            arrayz = aux;
        }
    }
    private int[] combine(int[] a, int[] b){//DELETE STATIC
     /*   System.out.println("Entered combine with these two arrays: ");
        for(int i = 0; i< a.length; i++){
            System.out.print(a[i]+" ");
        }
        System.out.println();
        for(int i = 0; i< b.length; i++){
            System.out.print(b[i]+" ");
        }
        System.out.println();*/
        int[] combined = new int[a.length+b.length];
        int aIndex = 0;
        int bIndex = 0;
        int combinedIndex = 0;
      
        while(aIndex<a.length&&bIndex<b.length){
            if(a[aIndex]<b[bIndex]){
                combined[combinedIndex] = a[aIndex];
                aIndex++;
                combinedIndex++;
            }
            else{
                combined[combinedIndex] = b[bIndex];
                bIndex++;
                combinedIndex++;
            }
        }
        while(aIndex<a.length){//dump the rest
            combined[combinedIndex] = a[aIndex];
            aIndex++;
            combinedIndex++;
        }
        while(bIndex<b.length){//dump the rest
            combined[combinedIndex] = b[bIndex];
            bIndex++;
            combinedIndex++;
        }
      /*  System.out.println("After Combined: ");
        for(int i = 0; i< combined.length; i++){
            System.out.print(combined[i]+" ");
        }
        System.out.println();*/
        combinedAMerge();
        return combined;

    }

  
}