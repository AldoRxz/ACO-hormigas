public class Methods {
    DataSet ds = new DataSet();
    boolean flag=false;
    
    void printMatrix(double [][]matrix){
        for(int row=0;row<matrix.length;row++){
            for(int column=0;column<matrix[0].length;column++){
                System.out.printf( "%.4f   \t",matrix[row][column]);
            }
            System.out.println("");
        }
    }


    void printProbability(double []array){
        for(int i=0;i<array.length;i++){
            System.out.printf("%.4f ",array[i]);
        }
    }


    void printRoute(int []array){
        for(int i=0;i<array.length;i++){
            System.out.printf("%d ",array[i]+1);
        }
        System.out.println("");
    }


    //The distance between cities is given by the following matrix assignment 
    double [][]initDistance(double [][]distance){
        double [][]aux={{0,  10, 12, 11, 14},
                        {10, 0,  13, 15, 8},
                        {12, 13, 0,  9,  14},
                        {11, 15, 9,  0,  16},
                        {14, 8,  14, 16, 0}};
        distance=aux;
        return distance;
    }
    //For each segment between cities we give the initial value: 1. Suppose this value is the same for all segments.
    double [][]initTau(double [][]tau){
        for(int row=0;row<tau.length;row++){
            for(int column=0;column<tau[0].length;column++){
                tau[row][column]=1;
            }
        }
        return tau;
    }
    //We determine the visibility as η_i,j = 1/L_i,j, between the cities by taking the inverse of the distance, 1/d
    double [][]initEta(double [][]distance){
        for(int row=0;row<distance.length;row++){
            for(int column=0;column<distance[0].length;column++){
                if(distance[row][column]==0){
                    distance[row][column]=0;
                }else{
                    distance[row][column]=1/(distance[row][column]);
                }
            }
        }
        return distance; //now "eta"
    }


    int []calculateProbability(double [][]tau,double [][]eta){
        double sum=0; //To save Σ(τ_i,j)α * (η_i,j)β
        int fromCity=0; //To save current city
        int iterations=0; //
        double[]product; //To save the probabilities
        int length=eta.length;
        int []saveRoute=new int[ds.getCities()];
        
        do{
            if(iterations==0){
                fromCity=0; //Update current city -> Beginning city = 1
            }else{
                int band=-1;
                 
                for(int column=0;column<eta.length;column++){
                    if(eta[0][column]!=0){
                        band++;
                        if(band==fromCity){
                            fromCity=column;
                            break;
                        }
                    }else{
//                        System.out.println(df.format(eta[0][column])+"No es diferente y band= "+band);
                    }
                }
            }
            /* -As city 1 is chosen as the beginning city the, it will be taboo to visit again,
                so that the level of visibility od the city 1 is made = 0
                -It will be the same rule for each new current city
            */
            saveRoute[iterations]=fromCity;
            System.out.printf("Next city: %d\n",fromCity+1);

            System.out.println("\nNew visibility matrix:");
            for(int row=0;row<eta[0].length;row++){
                eta[row][fromCity]=0;
            }
            printMatrix(eta);


            //Calculate -> Σ(τ_i,j)α * (η_i,j)β
                sum=0;
                for(int toCity=0;toCity<ds.getTau()[0].length;toCity++){ //Let us suppose ant 1 for city 1 choose the next city
                    if(eta[fromCity][toCity]!=0){ //If current city isn´t 0, realize operation
                        sum+=Math.pow(tau[fromCity][toCity], ds.getAlpha()) * Math.pow(eta[fromCity][toCity], ds.getBeta());
                    }
                }
            System.out.printf("Total sum: %.4f ",sum);
            length--; //degree the length each iteration cuz there is 1 less city to visit  
            product=new double[length];
            int j=0;
           //Calculate -> (τ_i,j)α * (η_i,j)β / Σ(τ_i,j)α * (η_i,j)β
                for(int toCity=0;toCity<ds.getTau()[0].length;toCity++){ //Let us suppose ant 1 for city 1 choose the next city
                    if(eta[fromCity][toCity]!=0){ //If current city isn´t 0, realize operation
                        product[j]=(Math.pow(tau[fromCity][toCity], ds.getAlpha()) * Math.pow(eta[fromCity][toCity], ds.getBeta()))/sum;
                        j++;
                    }
                }
                System.out.println("");
                fromCity=roulette(product); //Update the new position to calculate the current City
                iterations++;
        }while(iterations<eta.length); 
        
        ds.setRouteAnt(saveRoute);
       return ds.getRouteAnt(); //Must return the rute 
    }


    int roulette(double []probability){
        int pos=0;
        for(int i=1;i<probability.length;i++){
            probability[i]=probability[i]+probability[i-1]; //Cumulative number of posibilities
        }
        System.out.print("Cumulative posibilities: ");
        printProbability(probability);
        double rand = Math.random(); //Ramdom value [0,1]
        System.out.printf("\nRandom number %.4f \n",rand);
        
        for(int i=probability.length-1;i>=0;i--){
            if(rand<probability[i]){
                pos=i;
            }
        }
        return pos;
    }


    double calculateTotalDistance(int []antRoute){ //The route matrix tell us the positions to shift into distance matrix
        double distance=0; //save the total distance value
        ds.setDistance(initDistance(ds.getDistance())); //Copy initial distance matrix on current distance matrix
        //sM(ds.getDistance());
        int row,column; //Aux to move into the distance matrix and get the current value 
        for(int i=0;i<antRoute.length-1;i++){
           
                if(i+1>=antRoute.length-1){
                    row=antRoute[i]; column=antRoute[i+1];
                    distance+=ds.getDistance()[row][column];
                    
                    row=antRoute[i+1]; column=antRoute[0];
                    distance+=ds.getDistance()[row][column];
                }else{
                    row=antRoute[i]; column=antRoute[i+1];
                    distance+=ds.getDistance()[row][column];
                }
        }
        System.out.println("Distance = "+distance+"\n");
        return distance;
    }



    /*Now the total distance(returned value by calculateTotalDistance method)is
      used to update the pheromone level τ_i,j
      using the form: τ^(new) <-- (1 - p)τ^(current) + Σ∆τ_i,j
    */
    double [][]pheromoneMatrix(double distance, double [][]tau, int []antRoute){
        double pheromone = 1/distance;
        if(flag==false){
            tau=evaporationRate(tau);
            flag=true;
        }
        
        int row,column; //Aux to move into the distance matrix and get the current value 
        for(int i=0;i<antRoute.length-1;i++){
           
                if(i+1>=antRoute.length-1){
                    row=antRoute[i]; column=antRoute[i+1];
                    tau[row][column]+=pheromone;
                    
                    row=antRoute[i+1]; column=antRoute[0];
                    tau[row][column]+=pheromone;
                }else{
                    row=antRoute[i]; column=antRoute[i+1];
                    tau[row][column]+=pheromone;
                }
        }
        ds.setTau(tau);
        return ds.getTau();
    }


    
    double [][]evaporationRate(double [][]tau){
        for(int row=0;row<tau.length;row++){
            for(int column=0;column<tau[0].length;column++){
                tau[row][column]=(1 - 0.5)*tau[row][column];
            }
        }
        return tau;
    }


    
    String convertToString(int []antRoute){
        String str="";
        
            for(int i=0;i<antRoute.length;i++){
                str+=String.valueOf(antRoute[i]+1)+"-";
            }
            str+=String.valueOf(antRoute[0]+1);
        return str;
    }
}
