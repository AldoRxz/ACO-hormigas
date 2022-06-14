public class AntColonyOptimization {


    public static void main(String[] args) {
        Methods m= new Methods();
        DataSet ds=new DataSet();
        boolean initialized = false;
        for(int i=0;i<3;i++){
        
            if(initialized==false){
                ds.setDistance(m.initDistance(ds.getDistance()));
                m.initTau(ds.getTau());
                initialized=true;
            }else{
                ds.setDistance(m.initDistance(ds.getDistance()));
            }

            ds.setEta(m.initEta(ds.getDistance()));
            System.out.println("\nMatrix visibility (H):");
                m.printMatrix(ds.getEta());

           ds.setRouteAnt(m.calculateProbability(ds.getTau(),ds.getEta()));
            System.out.printf("Route ant %d: ",i+1);
                m.printRoute(ds.getRouteAnt());


            m.pheromoneMatrix(m.calculateTotalDistance(ds.getRouteAnt()), ds.getTau(),ds.getRouteAnt());
            System.out.println("New Tau Matrix:");
            m.printMatrix(ds.getTau());
            
            System.out.println(m.convertToString(ds.getRouteAnt()));
        }
    }
    
}
