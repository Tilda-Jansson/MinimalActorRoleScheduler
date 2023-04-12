import java.util.*;


/**
 * Represents a Problem object, it includes the useful information from the input
 */
public class Problem {
	public int scenes; // Number of scenes
	public int actors; // Number of actors
	public int roles;  // Number of roles
    public int superActorIndex; // Lowest number for the superactors 
	public int LowestSuperActorNumber; // Lowest number for the superactors (non-modified), used to give the superactors consecutive numbers when printing the solution, k+1,k+2...
    public int[][] roleCanBePlayedBy; // Possible actors for each role
	public ArrayList<HashSet<Integer>> neighbours; // Roles that play against each other
    public ArrayList<HashSet<Integer>> rolesActorCanPlay; // Possible roles for each actor
    public ArrayList<ArrayList<Integer>> scenesForRole; // Scenes for each role
    public int[][] sceneIsPlayedBy; // Roles in each scene
	public boolean[][] roleCanBePlayedByCheck; // A checklist to keep track of which actors for each role that have been tested in the lokalsökning (when we try to modify our solution)


	/**
	 * The constructor
	 */
	public Problem(int roles, int scenes, int actors, int[][] roleCanBePlayedBy, ArrayList<HashSet<Integer>> neighbours, ArrayList<HashSet<Integer>> rolesActorCanPlay,  ArrayList<ArrayList<Integer>> scenesForRole, int[][] sceneIsPlayedBy, boolean[][] roleCanBePlayedByCheck) {
		// Save all parameters
		this.roles = roles; 
		this.scenes = scenes; 
		this.actors = actors; 
		this.roleCanBePlayedBy = roleCanBePlayedBy; 
		this.neighbours = neighbours;
		this.rolesActorCanPlay = rolesActorCanPlay; 
        this.scenesForRole = scenesForRole;
        this.sceneIsPlayedBy = sceneIsPlayedBy;
        this.superActorIndex = this.actors + 1;
		this.LowestSuperActorNumber = this.actors +1;
		this.roleCanBePlayedByCheck = roleCanBePlayedByCheck;
	}

}