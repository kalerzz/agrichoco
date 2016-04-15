import org.chocosolver.solver.Solver;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.constraints.nary.geost.*;
import org.chocosolver.solver.constraints.nary.geost.util.RandomProblemGenerator;

/**
 * AgriChoco 
 *
 * @authors Fabien Hervouet & Eric Bourreau
 */
public class GMain {

    public static void main(String[] args) {
    	RandomProblemGenerator r = new RandomProblemGenerator(3, 2, 2, 2, 10);
    	r.generateProb();
    	System.out.println(r.getModel());
    }
    
}