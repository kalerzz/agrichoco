import org.chocosolver.solver.Solver;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;
import org.chocosolver.solver.constraints.ICF; 

/**
 * AgriChoco 
 *
 * @authors Fabien Hervouet & Eric Bourreau
 */
public class Main {

    public static void main(String[] args) {
    	// nombre de planches
    	int p = 1;
    	// nombre de lignes par planche
    	int l = 4;
    	// échantillonnage temporel
    	int t = 12;
    	// nombre de caractéristiques [id, mois_min, mois_max, duree_culture]
    	int c = 4;
    	// nombre de légumes
    	int v = 8;
    	// légumes
    	int[][] legumes = new int[v][c];
    	// nombre d'occurrences max sur une planche pour un même légume sur "une année"
    	int[] nb_legumes = new int[v];
    	
    	/********* CONSTANTES **********/
    	for (int i = 0; i < nb_legumes.length; i++) {
			nb_legumes[i] = l*(int)((legumes[i][2]-legumes[i][1])/legumes[i][3]); 
		}
    	int nb_objets = nb_legumes[0];
    	for (int i = 1; i < nb_legumes.length; i++) {
			nb_objets += nb_legumes[i];
		}
    	
        /*********** SOLVER ***********/
        Solver solver = new Solver("agrichoco");
        
        /********** VARIABLES **********/
        
        //
        IntVar[][][] planches = new IntVar[p][][];
        IntVar[] p_planches = new IntVar[p*l*t];
        for (int i = 0; i < p; i++) {
			planches[i] = VF.boundedMatrix("planche", l, t, 0, legumes.length, solver);
			for (int x = 0; x < t; x++) {
				for (int y = 0; y < l; y++) {
					p_planches[x+(y*l)+(i*l*t)] = planches[i][x][y];
				}
			}
		}
        
        // positions x et y réelles 2d sur la p-planche
        IntVar[] x_position = VF.boundedArray("x_position", nb_objets*p, 0, t-1, solver);
        IntVar[] y_position = VF.boundedArray("y_position", nb_objets*p, 0, l-1, solver);
        
        // dimension de relaxation (soit z vaut 0 et le légume joue soit rejeté sur la ième planche virtuelle 
        IntVar[] z_position = new IntVar[nb_objets*p];
        for (int i = 0; i < z_position.length; i++) {
        	z_position[i] = VF.enumerated("z_position_"+i, new int[] {0, i}, solver);
		}
        
        // soit x' la position linéarisée de (x,z)
        IntVar[] xp_position = VF.boundedArray("x_position", nb_objets*p, 0, v*(t-1), solver);
        
        // booléens de positionnement du carré j de l'objet i sur la case k de la planche p
        // [000444311]
        IntVar[][] Bik_p = VF.boundedMatrix("bik_p", nb_objets*p, l*t, 0, 1, solver);
        IntVar[][] Bijk_p = VF.boundedMatrix("bijk_p", nb_objets*l*t*p, l, 0, 1, solver);
        
        
        /********** CONTRAINTES **********/
        
        // linéarisation de (x,z) en x'
        for (int i = 0; i < v; i++) {
        	ICF.scalar(new IntVar[] {x_position[i], z_position[i]}, new int[] {1, t-1}, xp_position[i]);
		}
        
        // geost i.e. placement en 2d sur la grille
        
        // connexion entre les booléens des carrés et les booléens des objets
        int u;
		for (int m = 0; m < p; m++) {
			for (int i = 0; i < nb_objets; i++) {
				for (int k = 0; k < l*t; k++) {
					u = k+(i*l*t)+(m*nb_objets*l*t); 
					//ICF.sum(Bijk_p[u], Bik_p[u]);
				}
			}
		}
		
		//
		for (int m = 0; m < p; m++) {
			for (int x = 0; x < t; x++) {
				for (int y = 0; y < l; y++) {
					int[] ids = new int[nb_objets];
					int b = 0;
					for (int id = 0; id < v; id++) {
						for (int q = 0; q < nb_legumes[id]; q++) {
							ids[b++] =  id;
						}
					}
					//ICF.scalar(Bik_p[m*i], ids, p_planches[m*l*t+x+(y*l)]);
				}
			}
		}
		
		
        
       
        
        
        /********* MISC **********/
        // 4. Indicates that all solutions should be print to the console
        Chatterbox.showSolutions(solver);
        
        // 5. Launch the resolution process
        solver.findSolution();
        
        // 6. Finally, outputs the resolution statistics
        Chatterbox.printStatistics(solver);
    }
}