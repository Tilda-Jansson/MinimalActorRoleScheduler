import java.util.*;

    public class Labb5 {
        Kattio io;
        // Problem object
        private Problem problem;
        // Best solution so far
        private Solution bestSolution;


        Labb5() {
            io = new Kattio(System.in, System.out);
            read();

            findBestSolution();
            
            printSolution();
            
            io.close();
        }
            
        /**
         * Reads input and initiates the Problem object problem that contains all the important information from the input.
         */
        private void read() {
            int roles = io.getInt();
            int scenes = io.getInt();
            int actors = io.getInt();
            
            ArrayList<ArrayList<Integer>> scenesForRole = new ArrayList<ArrayList<Integer>>(); // Scenes for each role, i.e. [role: scenes for that role]

            int[][] roleCanBePlayedBy = new int[roles][]; // Actors that can play a particular role
            boolean[][] roleCanBePlayedByCheck = new boolean[roles][]; // A checklist to keep track of which actors for each role that have been tested in the Lokalsökning (when we try to modify our solution)

            ArrayList<HashSet<Integer>> rolesActorCanPlay = new ArrayList<HashSet<Integer>>(); // Possible roles for each actor
            for (int i = 0; i < actors; ++i) { 
                rolesActorCanPlay.add(i, new HashSet<Integer>()); 
            }

            for (int i = 0; i < roles; i++) {
                int n = io.getInt();
      
                roleCanBePlayedBy[i] = new int[n]; //Role (i+1) can be played by these n subsequent actors
                roleCanBePlayedByCheck[i] = new boolean[n];
      
                for (int j = 0; j < n; j++) {
                    int x = io.getInt();
                    roleCanBePlayedBy[i][j] = x; // (Superactors not included)
                    roleCanBePlayedByCheck[i][j] = false;
                    rolesActorCanPlay.get(x-1).add(i+1);
                }
            }

            ArrayList<HashSet<Integer>> neighbours = new ArrayList<HashSet<Integer>>(); //Represents roles that play against each other
            for (int i = 0; i < roles; ++i) {
                neighbours.add(i, new HashSet<Integer>());
            }
    
            int[][] sceneIsPlayedBy = new int[scenes][]; // Roles for each scene
            for (int i = 0; i < scenes; ++i) { 
                int t = io.getInt(); // number of roles in the particular scene
 
                sceneIsPlayedBy[i] = new int[t]; //Scene (i+1) has these subsequent n roles starring

                int[] m = new int[t];
                for (int j = 0; j < t; ++j) {
                    int x = io.getInt(); // role in the scene
                    m[j] = x; // roles included in the scene i
                    sceneIsPlayedBy[i][j] = x;
                }
    
                // find all combinations of the roles in the scene in order to set the neighbours
                for (int a = 0; a < t; ++a) {
                    for (int b = 0; b < t; ++b) {
                        if (m[a] != m[b])
                            neighbours.get(m[a]-1).add(m[b]);
                    }
                }
            }
    
            for (int i = 0; i < roles; i++) {
                int x = i+1;
                scenesForRole.add(findAllScenesForRole(x, scenes, sceneIsPlayedBy));
            }
        
            problem = new Problem(roles, scenes, actors, roleCanBePlayedBy, neighbours, rolesActorCanPlay, scenesForRole, sceneIsPlayedBy, roleCanBePlayedByCheck); // Problem object
        }

        /**
         *  Method that finds all scenes for a particular role 
         */
        private ArrayList<Integer> findAllScenesForRole(int role, int scenes, int[][] sceneIsPlayedBy) {
            ArrayList<Integer> sce = new ArrayList<Integer>();
            for (int i = 0; i < scenes; i++) {
                boolean check = false;
                for (int j = 0; j < sceneIsPlayedBy[i].length; j++) {
                    if (sceneIsPlayedBy[i][j] == role) {
                        check = true;
                        break;
                    }
                }
                if (check) {
                    sce.add(i+1);
                }
            }
            return sce;
        }


        /**
        *  This method is used to check if all actors for all roles have been tested in the process of trying to modify the solution (in the lokalsökning)
        */
        public static boolean areAllTrue(boolean[][] array){
            for (boolean[] el : array){
                for(boolean b : el){
                    if(!b) {
                        return false;
                    }
                } 
            }
            return true;
        }


        /**
        *  Try to modify an existing solution by picking a random role and a random actor for that particular role and try to set the actor to play that role.
        *   If the solution was modified, return that one. If it was impossible to modify the solution, return the same solution.
        */
        public Solution modifySolution(Solution currentSolution) {
            Solution newSolution;
        
            int i = 0;
            boolean[][] roleCanBePlayedByCheckCopy = new boolean[problem.roles][];
            for (boolean[] el : problem.roleCanBePlayedByCheck) {
                roleCanBePlayedByCheckCopy[i] = el;
                i++;
		    }

		    while (true) {
                newSolution = new Solution(currentSolution); // create a copy of the input-solution
			    // Pick a random role
			    int role = (int) (1 + (problem.roles) * Math.random());

			    // Pick a random actor that can play the role
			    int size = problem.roleCanBePlayedBy[role-1].length; // The number actors that can play the role 
			    int actorIndex = (int) ((size) * Math.random());

			    int actor = problem.roleCanBePlayedBy[role-1][actorIndex]; // Pick one of these actors

                if (roleCanBePlayedByCheckCopy[role-1][actorIndex]){ 
                    if (areAllTrue(roleCanBePlayedByCheckCopy)){ //When there are no more actors and roles left to tryout - break
                        break;
                    }
                    continue;
                }

			    // Try to set the actor to play the role 
			    if ( newSolution.AssignActorToRole(role, actor)){
                    break;
                }

                roleCanBePlayedByCheckCopy[role-1][actorIndex] = true; // Update the checklist, so we don't check the same thing again (for 'this' solution) (actor can't play this role)

		    }
		    return newSolution;
	    }


        /**
        * Create a solution using a simple heuristic and try to improve it using simulated anneling
        *
        * Heuristic: Start with giving the divas 1 role each that dont appear in same scenes.
        * Then give actor 3 as many roles(linearly) as possible, same with 4 etc...
        * Then fill in the rest of the roles with super actors
	    */
	    public void findBestSolution() {
            Solver solver = new Solver(problem);
		    Solution currentSolution = solver.solve();
        
		    // Keep track of the best solution so far
		    bestSolution = new Solution(currentSolution);

            double lowerBound = 0.01;
            double itera = 1000;
		    double cooler = 0.005;

            while (itera > lowerBound) {
                itera = itera*(1 - cooler);

                // Modify the current solution
			    Solution newSolution = modifySolution(currentSolution); 

                // Calculates a value that represents frequence of the actors in the solution - the higher value the better
			    int currentVal = currentSolution.freq();
			    int newVal = newSolution.freq();
                

                // I början: lägre frekvens hos skådisarna -> lägre freq ->  newValue-value / större itera -> större chans att val ligger nära 1 eftersom uttrycket (newValue - value) / (itera) ligger nära 0.
                // I slutet : Högre frekvens hos skådisar -> större freq -> newValue-value / mindre itera -> större negativ kvot -> Mindre chans att val ligger nära 1 (att lösningen accepteras) eftersom uttrycket (newValue - value) / (itera) blir väldigt negativt.
       
                // Decide if we should keep the modified solution, even if it's worse (simulated anneling) 
                if (newVal > currentVal) { // If the new solution is better - accept it right awawy
                    currentSolution = new Solution(newSolution);
                }
                else{
                    double x = Math.exp((newVal - currentVal) / itera); // 
                    if (0.5 < x){
                        currentSolution = new Solution(newSolution);
                    }
                }

                // Keep track of the best solution found
			    if (currentSolution.size() < bestSolution.size()) {
				    bestSolution = new Solution(currentSolution);
			    }
            }

        }

        /* 
         * Function used to print the solution 
         */
        public void printSolution() {
            System.out.print(bestSolution);
        }
    

        public static void main(String args[]) {
            new Labb5();
        }

}
