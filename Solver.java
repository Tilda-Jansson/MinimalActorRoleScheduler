import java.util.*;

/**
 * A solver object provides a solution to the problem using a simple heuristic.
 * Heuristic: Start with giving the divas 1 role each that don't appear in same scenes.
 * Then give actor 3 as many roles(linearly) as possible, same with 4 etc...
 * Then fill in the rest of the roles with super actors
*/
public class Solver {
	// problem object
	private Problem problem;
	// A solution to the problem
	private Solution solution;

	/**
	 * Constructor that initialize the solution object
	 */
	public Solver(Problem p) {
		problem = p;
		solution = new Solution(p);
	}

	/**
	 * Solve the problem and provide a valid solution
	 */
	public Solution solve() {
		giveRolesToDivas();
        
        giveRolesToRestOfActors();

        giveRolesToSuperActors();

		return solution;
	}

    /*
     * When there are still roles remaining but all actors are depleted, 
     * the superactors are used to fill the rest of the roles
     */
    public void giveRolesToSuperActors() { 
        if (solution.roleCounter < problem.roles) { // there are still roles without an actor

            for (int i = 0; i < problem.roles; i++) {
                if (!solution.roleHasActor[i]) {
                    
                    solution.usedActor[problem.superActorIndex-1] = 1; 
                    solution.actorForRole[i] = problem.superActorIndex;
                    solution.rolesForActor.get(problem.superActorIndex - 1).add(i+1); // Superactors can only have one role each (Once a superactor's role is removed, the superactor won't be assigned a role again in further modification of the solution)
                    
                    problem.superActorIndex++;
                    solution.roleHasActor[i] = true;
                }
            }
        }
    }

    /* 
     * Method that gives non-diva actors roles through a simple heuristic.
     * The heuristic is to get all the roles that an actor can play into a list called rol, and linearly
     * go through the list and add roles to rol that don't coincide with other roles in rol.
    */
    public void giveRolesToRestOfActors() { 
        for (int i = 3; i < problem.actors+1; i++) {

            ArrayList<Integer> rol = findValidRolesForActor(i); // all roles assigned to actor i

            int amount = rol.size();
            if (amount == 0)
                continue;

            solution.usedActor[i-1] = amount;

            for(int role : rol){
                solution.actorForRole[role-1] = i;
                solution.roleHasActor[role-1] = true;
                solution.rolesForActor.get(i-1).add(role);
            }
            
            if (solution.roleCounter >= problem.roles) { //All the roles have been assigned, don't need to iterate anymore
                break;
            }
        }
    }

    /*
     * Method that gives the divas one role each.
     */
    public void giveRolesToDivas() { 
        int diva1 = 1;
        int diva2 = 2;

        HashSet<Integer> diva1Roles = problem.rolesActorCanPlay.get(diva1-1);
        HashSet<Integer> diva2Roles = problem.rolesActorCanPlay.get(diva2-1);

        int[] rolesForDivas = findValidRolesForDivas(diva1Roles, diva2Roles);
        
        solution.actorForRole[rolesForDivas[0]-1] = 1; // Role assigned diva 1
        solution.rolesForActor.get(0).add(rolesForDivas[0]);
        solution.usedActor[0]++;
        solution.roleHasActor[rolesForDivas[0]-1] = true;

        solution.actorForRole[rolesForDivas[1]-1] = 2; // RRole assigned diva 2
        solution.rolesForActor.get(1).add(rolesForDivas[1]);
        solution.usedActor[1]++;
        solution.roleHasActor[rolesForDivas[1]-1] = true;

    }


    /* 
     * Find one role for each diva.
     * Done by going through all of the divas roles and checking if any of the roles from each list are valid, 
     * return those if it's the case.
     */
    public int[] findValidRolesForDivas(HashSet<Integer> actor1Roles, HashSet<Integer> actor2Roles) {
        for (int actor1Role : actor1Roles) {
            if (solution.roleHasActor[actor1Role-1]) { //Role already taken
                continue;
            }
            for (int actor2Role : actor2Roles) {
                if (solution.roleHasActor[actor2Role - 1]) { //Role already taken
                    continue;
                }
                if (rolesValid(actor1Role, actor2Role)) {
                    int[] arr = {actor1Role, actor2Role};
                    solution.roleCounter += 2;
                    return arr;
                }
            }
        }
        int[] arr = {-1, -1};
        return arr;
    }

    /*
     * Out of the possible roles an actor can play, pick as many roles as linearly possible for the actor that doesn't conflict
     */
    public ArrayList<Integer> findValidRolesForActor(int actor) { 
        HashSet<Integer> roles =  problem.rolesActorCanPlay.get(actor-1);
        ArrayList<Integer> validRoles = new ArrayList<Integer>();
        
        for (int role : roles) {
            if (solution.roleHasActor[role - 1]) { //Role already taken
                continue;
            }
            
            boolean check = true;
            for (Integer validRole : validRoles) {
                if (!rolesValid(role,validRole)) { // Check that the same actor can play the roles included in validRoles
                    check = false;
                    break;
                }
            }
            if (check) {
                solution.roleCounter++;
                validRoles.add(role);
            }
                
        }
        return validRoles;
    }

    /* 
     * Check that the roles don't have any scenes in common
     */
    public boolean rolesValid(int role1, int role2) {
        ArrayList<Integer> scenesForRole1 = problem.scenesForRole.get(role1-1);
        ArrayList<Integer> scenesForRole2 = problem.scenesForRole.get(role2-1);

        HashSet<Integer> common = new HashSet<Integer>(); // Scenes the roles have in common
        for (Integer t : scenesForRole1) {
            common.add(t);
        }
        for (Integer t : scenesForRole2) {
            if (common.contains(t)) {
                //These 2 roles have a scene in common, not valid
                 return false;
            }
        }
        return true; //These 2 roles don't have a scene in common, valid
    }

}