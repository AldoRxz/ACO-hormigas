public class DataSet {
    
    private int alpha = 1; // >= 0 is a parameter to control the influence of Tau (τ)
    private int  beta = 2; // >= 1 is a parameter to control the influence of eta (η)
    private int cities=5; //Let us suppose we have got a TSP problem with 5 cities
    private double [][]distance = new double[cities][cities]; //The next matrix will save the "distance" between cities
    private double [][]tau = new double[cities][cities]; //For each segment between "cities" we give initial pheromone value
    private double [][]eta  = new double[cities][cities];
    private int []routeAnt = new int[cities-1];

    public int[] getRouteAnt() {
        return routeAnt;
    }

    public void setRouteAnt(int[] routeAnt) {
        this.routeAnt = routeAnt;
    }

    public int getAlpha() {
        return alpha;
    }


    public int getBeta() {
        return beta;
    }

    public double[][] getEta() {
        return eta;
    }

    public void setEta(double[][] eta) {
        this.eta = eta;
    }

    public int getCities() {
        return cities;
    }

    
    public double[][] getDistance() {
        return distance;
    }

    public void setDistance(double[][] distance) {
        this.distance = distance;
    }

    public double[][] getTau() {
        return tau;
    }

    public void setTau(double[][] tau) {
        this.tau = tau;
    }
    
}
