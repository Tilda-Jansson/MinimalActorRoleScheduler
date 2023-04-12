# MinimalActorRoleScheduler

This is a code implementation of a scheduling problem for assigning actors to roles in a performance, with the goal of minimizing the number of actors required. The algorithm uses a simple heuristic and simulated annealing to find a suitable solution. The problem states that divas 1 and 2 (p1 and p2) must be assigned at least one role each and should not play against each other. Additionally, no monologues can occur in the performance, and the same actor cannot play different roles in the same scene.

## Problem Description

The goal is to find out which actors should be assigned which roles to solve the role assignment problem with as few actors as possible. The input parameters for the problem are:

* Roles r1, r2, ..., rn
* Actors p1, p2, ..., pk
* Condition type 1 (for each role): rt can be assigned to p1, p2, p6
* Condition type 2 (for each scene): in su, r1, r3, r5, r6, and r7 participate

## Input Format

The input consists of the following lines:

1. The first three lines are the numbers n, s, and k (number of roles, number of scenes, and number of actors, n≥1, s≥1, k≥2), one number per line.

2. The next n lines represent condition type 1 and start with a number indicating the number of subsequent numbers on the line, followed by the possible actors' numbers (between 1 and k).

3. The last s lines represent condition type 2 and start with a number indicating the number of subsequent numbers on the line, followed by the numbers representing the different roles participating in each scene. Each role appears at most once on each such line, so the number of roles on a line is between 2 and n.

## Output Format

The output consists of the following lines:

1. The first line: the number of actors who have been assigned roles.

2. One line for each actor (who has been assigned roles) with the actor's number, the number of roles assigned to the actor, and the numbers of these roles.


The problem must be solved according to the following conditions: divas must be included but must not meet, no role can be played by multiple people, and no actor can play against themselves in any scene.

Up to n-1 special super-actors with numbers k+1, k+2, ... are can be used and are allowed. Each super-actor can play any role, but they can only play one role.


# Solution Overview

The algorithm starts by assigning roles to actors in a non-optimal manner using a heuristic. The heuristic assigns one role per actor that has no shared scenes. It then allocates as many roles as possible to actor 3, actor 4, and so on, until all roles are assigned. If there are any remaining roles, they are assigned to the super-actors.

The algorithm then iteratively improves this initial solution by calling the *findBestSolution()* method for a fixed number of iterations. In each iteration, the *modifySolution()* method is called to try and improve the current solution.

## Modifying the Solution

*modifySolution()* works by selecting a random role and a random actor capable of playing that role. The algorithm then attempts to assign this actor to the role using the *AssignActorToRole()* method. If the change would make the role's neighbors incompatible with the new actor, the algorithm tries to find a new compatible actor for each conflicting neighbor role. If it's impossible to assign the chosen actor to the chosen role or find a compatible actor for conflicting neighbor roles, the function returns false and the algorithm resets the solution, choosing a new random role and actor and repeating the process. The attempts to assign a new random actor to a random role continue until successful, or the method returns the unmodified solution if all actors for all roles have been tested and no attempt has succeeded.

## Evaluating Solutions

Each solution is given a score representing how good it is; a higher score indicates a better solution. A solution with fewer actors playing multiple roles has a higher score than a solution with many actors playing only a few roles each.

If a new solution has a higher score than the current one, it replaces the current solution. If the new solution has a lower score, it may still replace the current solution, with the probability of accepting worse solutions decreasing as the search for better solutions progresses (iterations). This is where simulated annealing is applied, allowing local changes even if the objective function's value becomes slightly worse.

If the current solution is better than the best solution found so far, the best solution is replaced with the current one. After the algorithm has finished iterating through *modifySolution()*, the best solution is printed.

## Usage

To run the code, simply execute the main script, and the algorithm will iterate through the solutions, printing the best solution found at the end of the process.


This code was written as part of a course in Algorithms, Data Structures, and Complexity. The problem on [kattis](https://kth.kattis.com/problems/kth.adk.castingheuristic)
