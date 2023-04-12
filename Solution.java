import java.util.*;
/**
 * The solution object includes the information for the solution
 */
public class Solution {
	// Actor for each role (Role : An actor)
	public int[] actorForRole;  
	// Roles for each actor (Actor plays these roles) (Actors: Roles for that actor)
	public ArrayList<ArrayList<Integer>> rolesForActor; 
	// Number of roles each actor has been assigned
	public int[] usedActor; 
    // See if the role is taken or not
    public boolean[] roleHasActor;
	// Used when assigning roles to superactors
    public int roleCounter; 
    // The problem this solution belongs to 
	private Problem problem; 

	/**
	 * Solution constructor
	 */
	public Solution(Problem p) {
		// Save all parameters
		this.problem = p;
        this.roleHasActor = new boolean[p.roles];
        this.roleCounter = 0;
		usedActor = new int[problem.roles + problem.actors];
		actorForRole = new int[problem.roles]; 
		
        rolesForActor = new ArrayList<ArrayList<Integer>>(problem.roles + problem.actors); 
        for (int i = 0; i < problem.roles + problem.actors; i++) {
			rolesForActor.add(new ArrayList<Integer>());
		}

    }


    // Method to create a copy of a solution object
    public Solution(Solution sol) {
		// Copy problem data
		problem = sol.problem;

        roleHasActor = new boolean[sol.problem.roles];
        System.arraycopy(sol.roleHasActor, 0, roleHasActor, 0, sol.roleHasActor.length);

		// Copy solution data
		usedActor = new int[sol.usedActor.length];
		System.arraycopy(sol.usedActor, 0, usedActor, 0, sol.usedActor.length);

		actorForRole = new int[sol.actorForRole.length];
		System.arraycopy(sol.actorForRole, 0, actorForRole, 0, sol.actorForRole.length);
        
        rolesForActor = new ArrayList<ArrayList<Integer>>(sol.rolesForActor.size());
		for (ArrayList<Integer> el : sol.rolesForActor) {
			rolesForActor.add(new ArrayList<Integer>(el));
		}
	}


    /**
	 * The size of the solution (number of actors used). 
	 */
	public int size() {
        int count = 0;
		for (ArrayList<Integer> roles : rolesForActor) {
			if (roles.size() > 0) {
				count++;
			}
		}
		return count;
	}


    /**
	 * Returns a value representing how good the solution is.
	 * A solution consisting of few actors that have many roles each returns a higher value (better solution), 
	 * as opposed to a solution with many actors that only have a few roles each which returns a lower value (worse solution).
	 */
	public int freq() {
		int count = 0;
		for (int val : usedActor) {
			count +=  val * val * 1000000;
		}
		return count;
	}

    /**
	 * Assign role to actor if possible, and change neighbours if necessary to get valid solution.
	 */
	public boolean AssignActorToRole(int role, int actor) {
        
		if (actorForRole[role-1] == actor) {
			// Actor already plays the role
			return false;
		}
        
		// The divas must be included
		if ((actorForRole[role-1] == 1 && usedActor[1-1] == 1) || 
			(actorForRole[role-1] == 2 && usedActor[2-1] == 1)) { 
			return false;
		}

        boolean check = false;
        for (int element : problem.roleCanBePlayedBy[role-1]) { // Possible actors for role
            if (element == actor) {
                check = true;
            }
        }
        // Check that it's a valid role for the actor
		if (!check) {
			return false;
		}
        
		

		if (rolesForActor.get(actorForRole[role-1]-1).remove((Integer) role)) { // Remove role from the previous actor
			usedActor[actorForRole[role-1]-1]--; // The actor that had the role previously now has one less
		}

		usedActor[actor-1]++; // Increase role counter for the new actor
		actorForRole[role-1] = actor;
		rolesForActor.get(actor-1).add(role);
        

		// See if any of the neighbour roles conflict with the new actor for the role
		for (Integer neighbour : problem.neighbours.get(role-1)) { 
            if (actorForRole[neighbour-1] == actor || actor == 1 && actorForRole[neighbour-1] == 2 || actor == 2 && actorForRole[neighbour-1] == 1) {
				// The neighbour conflicts - change the actor for that neighbour role
				
				// Return if it is not possible to change the actor of the conflicting role
				if ((actorForRole[neighbour-1] == 1 && usedActor[1-1] == 1) || 
			        (actorForRole[neighbour-1] == 2 && usedActor[2-1] == 1)) { //The divas must be included
			            return false;
		        }

				boolean noReplacementFound = true; // If we can't replace the conflicting actor for the neighbour role

				// Find a new actor val to replace the actor for the conflicting neighbour role if possible! 
                for (Integer val : problem.roleCanBePlayedBy[neighbour-1]) { // Conflicting role can be played by actor val

					if (actorForRole[neighbour-1] == val) {
						// If the actor already is val, we know it will cause a conflict
						continue; 
					}
					
					// See if the new actor val is valid for the neighbour role. We must check that val doesn't cause conflict with its neighbour roles.
                    boolean conflict = false; 	
					for (Integer neighboursNeighbour : problem.neighbours.get(neighbour-1)) { // Roller som konfliktande rollen möter
						
                        if (actorForRole[neighboursNeighbour-1] == val // Same actor for different roles in the same scene, not OK
							|| (actorForRole[neighboursNeighbour-1] == 1 && val == 2)  // Divas can't be in the same scene
							|| (actorForRole[neighboursNeighbour-1] == 2 && val == 1)){ 
							conflict = true;
							break; //We cannot replace the conflicting role's actor with val if val also causes conflicts
						}
					}

					if (!conflict) {  // If val is a valid actor for the conflicting neighbour (it didn't cause any conflicts)
						noReplacementFound = false;
						if (rolesForActor.get(actorForRole[neighbour-1]-1).remove((Integer) neighbour)) { // Remove role from the previous actor
							usedActor[actorForRole[neighbour-1]-1]--; // The actor that had the role previously now has one less
						}
						usedActor[val-1]++; // Increase role counter for actor
						actorForRole[neighbour-1] = val;
						rolesForActor.get(val-1).add(neighbour);
						break; // Found an actor to replace the conflicting role's actor with
						
					}

				}
				if (noReplacementFound){ // We couldn't replace the conflicting actor for the neighbour role
					return false;
				}

			}
		}

		return true;
	}



    @Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(size() + "\n");
		int i = 1;
		int k = problem.LowestSuperActorNumber;
		
        for (ArrayList<Integer> roles : rolesForActor) { // Actor: roles
			if (roles.size() > 0) {

				if(i >= problem.LowestSuperActorNumber){ // Get consecutive numbers for the superactors in the output
					i = k;
					k++;
				}
				sb.append((i) + " " + roles.size());  // i is the number of the actor
				for (Integer role : roles) {
					sb.append(" " + role);
				}
				sb.append("\n");
			} 
			i++;
		}
		return sb.toString();
	}
}